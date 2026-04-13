package com.sagittarius.order.application.service;

import com.sagittarius.common.event.OrderCreatedEvent;
import com.sagittarius.common.exception.BusinessException;
import com.sagittarius.order.adapter.persistence.entity.Order;
import com.sagittarius.order.adapter.persistence.entity.OrderLineItems;
import com.sagittarius.order.adapter.persistence.entity.OrderStatus;
import com.sagittarius.order.adapter.persistence.repository.OrderRepository;
import com.sagittarius.order.adapter.persistence.repository.OrderSpecification;
import com.sagittarius.order.application.dto.CartResponse;
import com.sagittarius.order.application.dto.CreateOrderRequest;
import com.sagittarius.order.application.dto.OrderResponse;
import com.sagittarius.order.application.exception.OrderErrorCode;
import com.sagittarius.order.infrastructure.client.CartClient;
import com.sagittarius.order.infrastructure.client.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OutboxService outboxService;
    private final ProductClient productClient;
    private final CartClient cartClient;

    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrders(String customerId, String email, OrderStatus status, Pageable pageable) {
        Specification<Order> spec = Specification.where(OrderSpecification.hasCustomerId(customerId))
                .and(OrderSpecification.containsEmail(email))
                .and(OrderSpecification.hasStatus(status));

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        return orderRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    public void cancelOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException(OrderErrorCode.CANNOT_CANCEL_SHIPPED_ORDER);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Order {} has been cancelled", orderNumber);
    }


    @Transactional
    public String createOrder(String userId, String email, CreateOrderRequest request) {
        log.info("Creating order for customer: {}", userId);

        CartResponse cart = cartClient.getCart(userId);
        if (cart == null || cart.items() == null || cart.items().isEmpty()) {
            throw new BusinessException(OrderErrorCode.CART_EMPTY);
        }

        String orderNumber = UUID.randomUUID().toString();
        BigDecimal realTotalAmount = BigDecimal.ZERO;
        List<OrderLineItems> items = new ArrayList<>();
        List<OrderCreatedEvent.OrderItem> eventItems = new ArrayList<>();

        for (CartResponse.CartItemResponse cartItem : cart.items()) {
            try {
                // refresh real price from product service
                BigDecimal realPrice = productClient.getProductPrice(cartItem.productId());

                items.add(OrderLineItems.builder()
                        .skuCode(cartItem.productId())
                        .price(realPrice)
                        .quantity(cartItem.quantity())
                        .build());

                eventItems.add(new OrderCreatedEvent.OrderItem(cartItem.productId(), cartItem.quantity(), realPrice));
                realTotalAmount = realTotalAmount.add(realPrice.multiply(BigDecimal.valueOf(cartItem.quantity())));

            } catch (Exception e) {
                log.error("Cannot take the product's price: {}", cartItem.productId());
                throw new BusinessException(OrderErrorCode.PRODUCT_PRICE_UNAVAILABLE);
            }
        }

        if (request.getAmount().compareTo(realTotalAmount) != 0) {
            log.warn("Warning: Frontend's price passed ({}) different from System price ({}). Replacing by System price", request.getAmount(), realTotalAmount);
        }

        // Saving order
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customerId(userId)
                .email(email)
                .shippingAddress(request.getShippingAddress())
                .totalAmount(realTotalAmount)
                .status(OrderStatus.PENDING)
                .orderLineItemsList(items)
                .build();

        Order savedOrder = orderRepository.save(order);

        // Saving event
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(savedOrder.getOrderNumber())
                .customerId(savedOrder.getCustomerId())
                .totalAmount(savedOrder.getTotalAmount())
                .items(eventItems)
                .build();

        outboxService.saveEvent(
                "ORDER",
                savedOrder.getOrderNumber(),
                "OrderCreated",
                event
        );

        try {
            cartClient.clearCart(userId);
            log.info("Cart has been cleared for user: {}", userId);
        } catch (Exception e) {
            log.error("Error when deleting the shopping cart (does not affect the order creation process): {}", e.getMessage());
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
