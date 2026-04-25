package com.sagittarius.payment.application.service;

import com.sagittarius.common.exception.BusinessException;
import com.sagittarius.payment.adapter.persistence.entity.CustomerBalanceEntity;
import com.sagittarius.payment.adapter.persistence.repository.CustomerBalanceRepository;
import com.sagittarius.payment.application.exception.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private final CustomerBalanceRepository balanceRepository;
    private final OutboxService outboxService;

    @Transactional
    public String openWallet(String customerId) {
        // Check a customer's wallet
        if (balanceRepository.existsById(customerId)) {
            throw new BusinessException(PaymentErrorCode.WALLET_ALREADY_EXISTS);
        }

        // Create a new wallet with credit by 0
        CustomerBalanceEntity newWallet = CustomerBalanceEntity.builder()
                .customerId(customerId)
                .balance(BigDecimal.ZERO)
                .build();
        balanceRepository.save(newWallet);

        // Save event
        outboxService.saveEvent(
                customerId,
                "WalletOpened",
                Map.of(
                        "message", "Ví đã được mở thành công",
                        "balance", 0
                )
        );
        log.info("Wallet opened successfully for customer: {}", customerId);
        return "Mở ví thành công cho khách hàng: " + customerId;
    }



}
