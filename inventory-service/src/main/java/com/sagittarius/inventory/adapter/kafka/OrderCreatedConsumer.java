package com.sagittarius.inventory.adapter.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.common.event.OrderCreatedEvent;
import com.sagittarius.inventory.adapter.persistence.repository.InventoryRepository;
import com.sagittarius.inventory.application.service.InventoryService;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCreatedConsumer {
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "inventory-group")
    @Transactional
    public void listen(String message) {
        log.info("Received Message in Inventory: {}", message);
        try {
            JsonNode rootNode = objectMapper.readTree(message);


            if (!rootNode.has("type") || !rootNode.has("data")) {
                log.warn("Unknown message format. Missing 'type' or 'data'");
                return;
            }
            String eventType = rootNode.get("type").asText();

            if ("OrderCreated".equals(eventType)) {
                log.info("Received OrderCreated event");

                JsonNode dataNode = rootNode.get("data");

                String orderNumber = rootNode.has("orderId")
                        ? rootNode.get("orderId").asText()
                        : dataNode.get("orderId").asText();

                String customerId = "";
                BigDecimal totalAmount = BigDecimal.ZERO;
                if (dataNode.has("customerId")) {
                    customerId = dataNode.get("customerId").asText();
                }
                if (dataNode.has("totalAmount")) {
                    totalAmount = new BigDecimal(dataNode.get("totalAmount").asText());
                }

                Map<String, Integer> itemsMap = new HashMap<>();
                JsonNode items = dataNode.get("items");
                if (items != null && items.isArray()) {
                    for (JsonNode item : items) {
                        itemsMap.put(
                                item.get("productId").asText(),
                                item.get("quantity").asInt());
                    }
                }

                log.info("Processing Order: {} with items: {}", orderNumber, itemsMap);
                inventoryService.processOrderEvent(orderNumber, customerId, totalAmount, itemsMap);
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
            // TODO: Gửi sự kiện 'InventoryFailed' sang Kafka
        }
    }

    private void handleOrderCreated(JsonNode payload) {

    }
}
