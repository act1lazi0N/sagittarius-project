package com.sagittarius.cart.adapter.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.cart.application.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final CartService cartService;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(topics = "order-events", groupId = "cart-group")
    public void listen(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            if (!rootNode.has("type") || !rootNode.has("data")) return;

            String eventType = rootNode.get("type").asText();

            // Delete Cart when the order has been created
            if ("OrderCreated".equals(eventType)) {
                JsonNode dataNode = rootNode.get("data");
                if (dataNode.has("customerId")) {
                    String customerId = dataNode.get("customerId").asText();
                    log.info("Event OrderCreated catched! Removing user cart: {}", customerId);
                    cartService.clearCart(customerId);
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý sự kiện Order cho Giỏ hàng", e);
        }
    }
}
