package com.sagittarius.inventory.adapter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.common.event.OrderCreatedEvent;
import com.sagittarius.inventory.application.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCreatedConsumer {
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    @KafkaListener(topics = "order-events", groupId = "inventory-group")
    public void listen(String message)
    {
        log.info("Received Message in Inventory: {}", message);
        try
        {
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            log.info("Processing allocation for OrderId: {}, Item Count: {}",
                    event.getOrderId(), event.getItems() != null ? event.getItems().size() : 0);
            inventoryService.handleOrderCreated(event);
        } catch (Exception e)
        {
            log.error("Error processing message: {}", message, e);
        }
    }
}
