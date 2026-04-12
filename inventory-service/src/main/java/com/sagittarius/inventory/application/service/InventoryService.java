package com.sagittarius.inventory.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.inventory.adapter.persistence.entity.Inventory;
import com.sagittarius.inventory.adapter.persistence.entity.Outbox;
import com.sagittarius.inventory.adapter.persistence.entity.ProcessedOrderEntity;
import com.sagittarius.inventory.adapter.persistence.repository.InventoryRepository;
import com.sagittarius.inventory.adapter.persistence.repository.OutboxRepository;
import com.sagittarius.inventory.adapter.persistence.repository.ProcessedOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProcessedOrderRepository processedOrderRepository;
    private final OutboxService outboxService;
    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode, Integer quantity) {
        return inventoryRepository.findBySkuCode(skuCode)
                .map(inventory -> inventory.getAvailableStock() >= quantity)
                .orElse(false);
    }

    @Transactional()
    public void stockIn(String skuCode, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseGet(() -> Inventory.builder()
                        .skuCode(skuCode)
                        .quantity(0)
                        .reservedQuantity(0)
                        .reorderLevel(10) // Default reorder level (Warning nearly insufficient items)
                        .build());
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
        log.info("Stock added for {}: {}. Total: {}", skuCode, quantity, inventory.getQuantity());
    }

    @Transactional(readOnly = true)
    public void processOrderEvent(String orderNumber, String customerId, BigDecimal totalAmount, Map<String, Integer> items) {
        if (processedOrderRepository.existsById(orderNumber)) {
            log.info("Order {} already processed. Skipping.", orderNumber);
            return;
        }

        // Check and reduce inventory
        try {
            boolean allAvailable = true;

            // Storage decrease items
            List<Inventory> inventoriesToUpdate = new ArrayList<>();

            // Check item
            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                String skuCode = entry.getKey();
                Integer quantity = entry.getValue();

                Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + skuCode));

                if (inventory.getAvailableStock() < quantity) {
                    allAvailable = false;
                    break;
                } else {
                    inventory.reserveStock(quantity);
                    inventoriesToUpdate.add(inventory);
                }
            }

            if (allAvailable) {
                inventoryRepository.saveAll(inventoriesToUpdate);

                // Packaging payload -> sending to Payment
                Map<String, Object> payloadData = new HashMap<>();
                payloadData.put("items", items);
                payloadData.put("customerId", customerId);
                payloadData.put("totalAmount", totalAmount);
                outboxService.saveEvent(
                        "INVENTORY",
                        orderNumber,
                        "InventoryReserved",
                        payloadData // Decreased item list
                );
                log.info("SUCCESS: Stock reserved & deducted for order {}", orderNumber);
            } else {
                log.warn("FAILED: Out of stock for order {}", orderNumber);
            }

            processedOrderRepository.save(new ProcessedOrderEntity(orderNumber));
        } catch (Exception e) {
            log.error("Error processing inventory", e);
            throw e;
        }
    }

    @Transactional
    public void cancelOrderReservation(String orderNumber, Map<String, Integer> items) {
        String cancelEventId = orderNumber + "_CANCEL";

        if (processedOrderRepository.existsById(cancelEventId)) {
            log.info("Order {} already processed. Skipping.", cancelEventId);
            return;
        }

        try {
            List<Inventory> inventoriesToUpdate = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                String skuCode = entry.getKey();
                Integer quantity = entry.getValue();

                inventoryRepository.findBySkuCode(skuCode).ifPresent(inventory -> {
                    inventory.cancelReservation(quantity);
                    inventoriesToUpdate.add(inventory);
                });
            }
            inventoryRepository.saveAll(inventoriesToUpdate);
            processedOrderRepository.save(new ProcessedOrderEntity(cancelEventId));

            log.info("Stock reservation successfully cancelled and returned to inventory for order {}", orderNumber);
        } catch (Exception e) {
            log.error("Error cancelling inventory reservation for order {}", orderNumber, e);
            throw e;
        }
    }
}
