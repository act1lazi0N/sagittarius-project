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
import java.util.Map;
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
    private final OutboxService outboxService;

    @Transactional
    @SneakyThrows
    public void processPayment(String orderId, String customerId, BigDecimal amount) {
        if (processedOrderRepository.existsById(orderId)) {
            log.warn("Payment for Order {} already processed.", orderId);
            return;
        }
        try {
            CustomerBalanceEntity wallet = balanceRepository.findById(customerId).orElse(null);

            boolean isSuccess = false;
            String reason = "";

            if (wallet != null && wallet.getBalance().compareTo(amount) >= 0) {
                wallet.setBalance(wallet.getBalance().subtract(amount));
                balanceRepository.save(wallet);
                isSuccess = true;
                log.info("Payment success for Order {}. Deducted {}. New balance: {}", orderId, amount, wallet.getBalance());
            } else {
                log.warn("Insufficient fund for Order {}. Customer: {}. Amount: {}", orderId, customerId, amount);
            }
            PaymentEntity payment = PaymentEntity.builder()
                    .id(UUID.randomUUID())
                    .orderId(orderId)
                    .customerId(customerId)
                    .amount(amount)
                    .status(isSuccess ? "SUCCESS" : "FAILED")
                    .createdAt(LocalDateTime.now())
                    .build();
            paymentRepository.save(payment);

            if (isSuccess) {
                outboxService.saveEvent(orderId, "PaymentCompleted", Map.of("paymentId", payment.getId()));
                log.info("Payment SUCCESS for order {}. Deducted {}", orderId, amount);
            } else {
                outboxService.saveEvent(orderId, "PaymentFailed", Map.of("reason", reason));
                log.warn("Payment FAILED for order {}. Reason: {}", orderId, reason);
            }

            processedOrderRepository.save(new ProcessedOrderEntity(orderId, LocalDateTime.now()));
        }
        catch (Exception ex) {
            log.error("Error processing payment for order {}", orderId, ex);
            throw ex; // Rollback transaction
        }
    }
}
