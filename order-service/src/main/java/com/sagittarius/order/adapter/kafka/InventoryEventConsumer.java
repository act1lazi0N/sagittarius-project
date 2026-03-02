package com.sagittarius.order.adapter.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.order.adapter.persistence.entity.Order;
import com.sagittarius.order.adapter.persistence.entity.OrderStatus;
import com.sagittarius.order.adapter.persistence.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inventory-events", groupId = "order-group")
    @Transactional
    public void listen(String message) {
        log.info("Order Service received Inventory Event: {}", message);
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            if (!rootNode.has("type") || !rootNode.has("orderId")) {
                log.warn("Invalid message format (missing type or orderId): {}", message);
                return;
            }
            if (!rootNode.has("data")) {
                log.warn("Warning: Payment message missing 'data' payload");
            }

            String eventType = rootNode.get("type").asText();
            String orderNumber = rootNode.get("orderId").asText();

            Order order = orderRepository.findByOrderNumber(orderNumber)
                    .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));

            if ("InventoryReserved".equals(eventType)) {
                order.setStatus(OrderStatus.CONFIRMED);
                log.info("Order {} confirmed (Stock reserved)", orderNumber);
            } else if ("InventoryFailed".equals(eventType)) {
                order.setStatus(OrderStatus.CANCELLED);
                log.info("Order {} cancelled (Out of stock)", orderNumber);
            }
            orderRepository.save(order);


        } catch (JsonProcessingException e) {
            log.error("Error processing inventory event", e);
        }
    }
}
