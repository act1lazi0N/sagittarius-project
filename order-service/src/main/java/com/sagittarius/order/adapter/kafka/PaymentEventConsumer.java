package com.sagittarius.order.adapter.kafka;

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

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-events", groupId = "order-group")
    @Transactional
    public void listen(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);

            if (!rootNode.has("type") || !rootNode.has("payload")) {
                log.warn("Invalid payment message structure");
                return;
            }

            String eventType = rootNode.get("type").asText();
            String payloadStr = rootNode.get("payload").asText();

            JsonNode payload = objectMapper.readTree(payloadStr);
            String orderNumber = payload.get("orderId").asText();

            Order order = orderRepository.findByOrderNumber(orderNumber)
                    .orElse(null);

            if (order == null) {
                log.warn("Order not found with number: {}", orderNumber);
                return;
            }

            if ("PaymentProcessed".equals(eventType)) {
                order.setStatus(OrderStatus.PAID);
                log.info("Order {} updated to PAID", orderNumber);
            } else if ("PaymentFailed".equals(eventType)) {
                order.setStatus(OrderStatus.CANCELLED);
                log.info("Order {} updated to CANCELLED due to payment failure", orderNumber);
            }

            orderRepository.save(order);

        } catch (Exception e) {
            log.error("Error processing payment event", e);
        }
    }
}
