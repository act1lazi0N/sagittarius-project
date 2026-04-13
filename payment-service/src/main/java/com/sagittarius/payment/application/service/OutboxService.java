package com.sagittarius.payment.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.common.exception.BusinessException;
import com.sagittarius.payment.adapter.persistence.entity.OutboxEntity;
import com.sagittarius.payment.adapter.persistence.repository.OutboxRepository;
import com.sagittarius.payment.application.exception.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveEvent(String aggregateId, String eventType, Object payloadData) {
        try {
            Map<String, Object> eventWrapper = new HashMap<>();
            eventWrapper.put("type", eventType);
            eventWrapper.put("orderId", aggregateId);
            eventWrapper.put("data", payloadData);

            String payload = objectMapper.writeValueAsString(eventWrapper);

            OutboxEntity outbox = OutboxEntity.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("PAYMENT")
                    .aggregateId(aggregateId)
                    .type(eventType)
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(outbox);
            log.info("Outbox event saved: Type={}, OrderId={}", eventType, aggregateId);
        } catch (JsonProcessingException e) {
            log.error("Error serializing payment outbox payload", e);
            throw new BusinessException(PaymentErrorCode.JSON_PROCESS_ERROR);
        }
    }

}
