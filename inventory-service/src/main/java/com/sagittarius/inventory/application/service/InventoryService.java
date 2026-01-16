package com.sagittarius.inventory.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.common.event.OrderCreatedEvent;
import com.sagittarius.inventory.adapter.persistence.entity.OutboxEntity;
import com.sagittarius.inventory.adapter.persistence.entity.ProcessedOrderEntity;
import com.sagittarius.inventory.adapter.persistence.entity.ProductEntity;
import com.sagittarius.inventory.adapter.persistence.repository.InventoryRepository;
import com.sagittarius.inventory.adapter.persistence.repository.OutboxRepository;
import com.sagittarius.inventory.adapter.persistence.repository.ProcessedOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProcessedOrderRepository processedOrderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @SneakyThrows
    public void handleOrderCreated(OrderCreatedEvent event)
    {
        String orderId = event.getOrderId();
        if (processedOrderRepository.existsById(orderId)) {
            log.warn("Order {} already processed. Skipping.", orderId);
            return;
        }
        if (event.getItems() == null || event.getItems().isEmpty()) {
            return;
        }
        var item = event.getItems().getFirst();
        String sku = item.getProductId();
        int qtyRequested = item.getQuantity();

        ProductEntity product = inventoryRepository.findByIdAndLock(sku).orElse(null);
        boolean success = false;

        if (product != null && product.getAvailableQuantity() >= qtyRequested)
        {
            product.setAvailableQuantity(product.getAvailableQuantity() - qtyRequested);
            inventoryRepository.save(product);
            success = true;
            log.info("Stock deducted for Order {}. SKU: {}, New Qty: {}", orderId, sku, product.getAvailableQuantity());
        }
        else
        {
            log.warn("Insufficient stock for Order {}. SKU: {}, Requested: {}", orderId, sku, qtyRequested);
        }

        String eventType = success ? "InventoryReserved" : "InventoryReservationFailed";
        var replyPayload = new InventoryEventPayload(
                orderId,
                event.getCustomerId(),
                event.getTotalAmount(),
                success ? "SUCCESS" : "OUT_OF_STOCK"
        );

        OutboxEntity outbox = OutboxEntity
                .builder()
                .id(UUID.randomUUID())
                .aggregateType("INVENTORY")
                .aggregateId(orderId)
                .type(eventType)
                .payload(objectMapper.writeValueAsString(replyPayload))
                .createdAt(LocalDateTime.now())
                .build();

        outboxRepository.save(outbox);
        processedOrderRepository.save(new ProcessedOrderEntity(orderId, LocalDateTime.now()));
    }
    record InventoryEventPayload(String orderId, String customerId, BigDecimal amount, String reason) {}
}
