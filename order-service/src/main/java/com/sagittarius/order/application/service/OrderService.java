package com.sagittarius.order.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.common.event.OrderCreatedEvent;
import com.sagittarius.common.exception.BusinessException;
import com.sagittarius.order.adapter.persistence.entity.Order;
import com.sagittarius.order.adapter.persistence.entity.OrderLineItems;
import com.sagittarius.order.adapter.persistence.entity.OrderStatus;
import com.sagittarius.order.adapter.persistence.entity.Outbox;
import com.sagittarius.order.adapter.persistence.repository.OrderRepository;
import com.sagittarius.order.adapter.persistence.repository.OrderSpecification;
import com.sagittarius.order.adapter.persistence.repository.OutboxRepository;
import com.sagittarius.order.adapter.web.dto.CreateOrderRequest;
import com.sagittarius.order.adapter.web.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrders(String customerId, String email, OrderStatus status, Pageable pageable) {
        Specification<Order> spec = Specification.where(OrderSpecification.hasCustomerId(customerId))
                .and(OrderSpecification.containsEmail(email))
                .and(OrderSpecification.hasStatus(status));

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        return orderPage.map(this::mapToResponse);
    }

    public void cancelOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel order that has been shipped");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Order {} has been cancelled", orderNumber);
    }


    @Transactional
    public String createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());

        String orderNumber = UUID.randomUUID().toString();

        List<OrderLineItems> items = request.getItems().stream()
                .map(item -> OrderLineItems.builder()
                        .skuCode(item.getProductId())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customerId(request.getCustomerId())
                .email(request.getEmail())
                .shippingAddress(request.getShippingAddress())
                .totalAmount(request.getAmount())
                .status(OrderStatus.PENDING)
                .orderLineItemsList(items)
                .build();

        Order savedOrder = orderRepository.save(order);

        try {
            List<OrderCreatedEvent.OrderItem> eventItems = request.getItems().stream()
                    .map(item -> new OrderCreatedEvent.OrderItem(item.getProductId(), item.getQuantity(), item.getPrice()))
                    .toList();

            OrderCreatedEvent event = OrderCreatedEvent.builder()
                    .orderId(savedOrder.getOrderNumber())
                    .customerId(savedOrder.getCustomerId())
                    .totalAmount(savedOrder.getTotalAmount())
                    .items(eventItems)
                    .build();

            String payload = objectMapper.writeValueAsString(event);

            Outbox outbox = Outbox.builder()
                    .aggregateType("ORDER")
                    .aggregateId(savedOrder.getOrderNumber())
                    .type("OrderCreated")
                    .payload(payload)
                    .build();

            outboxRepository.save(outbox);
            log.info("FINISHED SAVING OUTBOX");

        } catch (JsonProcessingException e) {
            throw new BusinessException("Error processing JSON for Outbox event");
        }

        return savedOrder.getOrderNumber();
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderResponse.OrderItemResponse> items = order.getOrderLineItemsList().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .skuCode(item.getSkuCode())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build()).toList();
        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .email(order.getEmail())
                .shippingAddress(order.getShippingAddress())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}
