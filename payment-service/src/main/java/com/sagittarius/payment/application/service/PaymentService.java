package com.sagittarius.payment.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.payment.adapter.persistence.entity.CustomerBalanceEntity;
import com.sagittarius.payment.adapter.persistence.entity.OutboxEntity;
import com.sagittarius.payment.adapter.persistence.entity.PaymentEntity;
import com.sagittarius.payment.adapter.persistence.entity.ProcessedOrderEntity;
import com.sagittarius.payment.adapter.persistence.repository.CustomerBalanceRepository;
import com.sagittarius.payment.adapter.persistence.repository.OutboxRepository;
import com.sagittarius.payment.adapter.persistence.repository.PaymentRepository;
import com.sagittarius.payment.adapter.persistence.repository.ProcessedOrderRepository;
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
public class PaymentService {
    private final CustomerBalanceRepository balanceRepository;
    private final PaymentRepository paymentRepository;
    private final ProcessedOrderRepository processedOrderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @SneakyThrows
    public void processPayment(String orderId, String customerId, BigDecimal amount) {
        if (processedOrderRepository.existsById(orderId)) {
            log.warn("Payment for Order {} already processed.", orderId);
            return;
        }

        CustomerBalanceEntity balanceEntity = balanceRepository.findByIdAndLock(customerId).orElse(null);
        boolean isSuccess = false;
        String status = "FAILED";

        if (balanceEntity != null && balanceEntity.getBalance().compareTo(amount) >= 0) {
            balanceEntity.setBalance(balanceEntity.getBalance().subtract(amount));
            balanceRepository.save(balanceEntity);
            isSuccess = true;
            status = "COMPLETED";
            log.info("Payment success for Order {}. Deducted {}. New balance: {}", orderId, amount, balanceEntity.getBalance());
        } else {
            log.warn("Insufficient fund for Order {}. Customer: {}. Amount: {}", orderId, customerId, amount);
        }

        PaymentEntity payment = PaymentEntity.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .customerId(customerId)
                .amount(amount)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        String eventType = isSuccess ? "PaymentProcessed" : "PaymentFailed";
        var payload = new PaymentEventPayload(orderId, status);

        OutboxEntity outbox = OutboxEntity.builder()
                .id(UUID.randomUUID())
                .aggregateType("PAYMENT")
                .aggregateId(orderId)
                .type(eventType)
                .payload(objectMapper.writeValueAsString(payload))
                .createdAt(LocalDateTime.now())
                .build();
        outboxRepository.save(outbox);
        processedOrderRepository.save(new ProcessedOrderEntity(orderId, LocalDateTime.now()));
    }
    record PaymentEventPayload(String orderId, String status) {}
}
