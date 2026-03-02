package com.sagittarius.order.adapter.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.order.adapter.persistence.entity.Order;
import com.sagittarius.order.adapter.persistence.entity.OrderStatus;
import com.sagittarius.order.adapter.persistence.repository.OrderRepository;
import com.sagittarius.order.application.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final OutboxService outboxService;

    @KafkaListener(topics = "payment-events", groupId = "order-group")
    @Transactional
    public void listen(String message) {
        log.info("Order Service received Payment event: {}", message);
        try {
            JsonNode rootNode = objectMapper.readTree(message);

            if (!rootNode.has("type") || !rootNode.has("aggregateId")) {
                log.warn("Invalid message structure: Missing 'type' or 'aggregateId'");
                return;
            }
            String eventType = rootNode.get("type").asText();
            String orderNumber = rootNode.get("aggregateId").asText();

            if (orderNumber.isBlank() || eventType.isBlank()) {
                log.warn("Unknown message format: Empty type or aggregateId");
                return;
            }

            Order order = orderRepository.findByOrderNumber(orderNumber)
                    .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));

            Map<String, Integer> itemsMap = new HashMap<>();
            if (order.getOrderLineItemsList() != null) {
                order.getOrderLineItemsList().forEach(item -> {
                    itemsMap.put(item.getSkuCode(), item.getQuantity());
                });
            }

            if ("PaymentCompleted".equals(eventType) || "PaymentProcessed".equals(eventType)) {
                order.setStatus(OrderStatus.PAID);
                log.info("Order {} updated to PAID", orderNumber);
                outboxService.saveEvent("ORDER", orderNumber, "OrderCompleted", Map.of("items", itemsMap));
            } else if ("PaymentFailed".equals(eventType)) {
                order.setStatus(OrderStatus.CANCELLED);
                String reason = "Unknown";
                if (rootNode.has("payload")) {
                    JsonNode payloadNode = objectMapper.readTree(rootNode.get("payload").asText());
                    if (payloadNode.has("data") && payloadNode.get("data").has("reason")) {
                        reason = payloadNode.get("data").get("reason").asText();
                    }
                }
                log.warn("Cancelled Order {}: {}", orderNumber, reason);

                outboxService.saveEvent("ORDER", orderNumber, "OrderCancelled", Map.of("items", itemsMap));
                log.info("Order {} updated to CANCELLED due to payment failure", orderNumber);
            } else {
                log.info("Ignored unhandled event type: {}", eventType);
            }

            orderRepository.save(order);

        } catch (Exception e) {
            log.error("Error processing payment event", e);
        }
    }
}
