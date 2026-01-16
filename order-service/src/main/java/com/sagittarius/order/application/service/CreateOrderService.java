package com.sagittarius.order.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.common.event.OrderCreatedEvent;
import com.sagittarius.order.adapter.persistence.entity.OrderEntity;
import com.sagittarius.order.adapter.persistence.entity.OutboxEntity;
import com.sagittarius.order.adapter.persistence.repository.OrderRepository;
import com.sagittarius.order.adapter.persistence.repository.OutboxRepository;
import com.sagittarius.order.adapter.web.dto.CreateOrderRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrderService {
    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @SneakyThrows
    public UUID createOrder(CreateOrderRequest request)
    {
        log.info("Start creating order for customerId={} with amount={}", request.getCustomerId(), request.getAmount());
        UUID orderId = UUID.randomUUID();
        OrderEntity orderEntity = OrderEntity.builder()
                .id(orderId)
                .customerId(request.getCustomerId())
                .totalAmount(request.getAmount())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        orderRepository.save(orderEntity);
        log.info("Order entity saved with id={}", orderId);

        List<OrderCreatedEvent.OrderItem> eventItems = request.getItems().stream()
                .map(item -> new OrderCreatedEvent.OrderItem(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice()
                )).toList();

        OrderCreatedEvent event = OrderCreatedEvent
                .builder()
                .orderId(orderId.toString())
                .customerId(request.getCustomerId())
                .totalAmount(request.getAmount())
                .items(eventItems)
                .build();

        String eventPayload = objectMapper.writeValueAsString(event);

        OutboxEntity outbox = OutboxEntity.builder()
                .id(UUID.randomUUID())
                .aggregateType("ORDER")
                .aggregateId(orderId.toString())
                .type("OrderCreated")
                .payload(eventPayload)
                .createdAt(LocalDateTime.now())
                .build();

        outboxRepository.save(outbox);
        log.debug("Outbox event saved for orderId={}. Payload: {}", orderId, eventPayload);

        log.info("Order creation completed successfully. Transaction committed for orderId={}", orderId);
        return orderId;
    }
}
