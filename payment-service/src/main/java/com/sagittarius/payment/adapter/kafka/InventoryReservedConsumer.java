package com.sagittarius.payment.adapter.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.payment.application.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryReservedConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @KafkaListener(topics = "inventory-events", groupId = "payment-group")
    public void listen(String message) {
       try
       {
           JsonNode rootNode = objectMapper.readTree(message);
           if (!rootNode.has("type") || !rootNode.has("payload")) {
               log.warn("Invalid message structure. Missing 'type' or 'payload'");
               return;
           }

           String eventType = rootNode.get("type").asText();
           if (!"InventoryReserved".equals(eventType)) {
               return;
           }

           String innerPayloadStr = rootNode.get("payload").asText();
           JsonNode payloadNode = objectMapper.readTree(innerPayloadStr);

           String orderId = payloadNode.get("orderId").asText();
           String customerId = payloadNode.get("customerId").asText();
           BigDecimal amount = new BigDecimal(payloadNode.get("amount").asText());

           log.info("Received InventoryReserved for Order {}", orderId);

           paymentService.processPayment(orderId, customerId, amount);
       }
       catch (Exception e)
       {
           log.error("Error processing inventory event", e);
       }
    }
}
