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
           if (!rootNode.has("type") || !rootNode.has("orderId") || !rootNode.has("data")) {
               log.warn("Invalid message structure. Missing 'type', 'orderId' or 'data'");
               return;
           }

           String eventType = rootNode.get("type").asText();
           if ("InventoryReserved".equals(eventType)) {

               String orderId = rootNode.get("orderId").asText();
               JsonNode dataNode = rootNode.get("data");

               if (!dataNode.has("customerId") || !dataNode.has("totalAmount")) {
                   log.warn("Bỏ qua tin nhắn do thiếu customerId hoặc totalAmount: {}", message);
                   return;
               }

               String customerId = dataNode.get("customerId").asText();
               BigDecimal amount = new BigDecimal(dataNode.get("totalAmount").asText());

               log.info("Processing payment for Order: {}, Customer: {}, Amount: {}", orderId, customerId, amount);
               paymentService.processPayment(orderId, customerId, amount);
           }
       }
       catch (Exception e)
       {
           log.error("Error processing inventory event", e);
       }
    }
}
