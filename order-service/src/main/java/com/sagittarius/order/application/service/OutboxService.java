package com.sagittarius.order.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.common.exception.BusinessException;
import com.sagittarius.order.adapter.persistence.entity.Outbox;
import com.sagittarius.order.adapter.persistence.repository.OutboxRepository;
import com.sagittarius.order.application.exception.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     *
     * @param aggregateType: Object (ORDER)
     * @param aggregateId: Object ID
     * @param eventType: Event (Create, Delete...)
     * @param payloadData: DATA
     */
    @Transactional
    public void saveEvent(String aggregateType, String aggregateId, String eventType, Object payloadData) {
        try {
            Map<String, Object> eventWrapper = new HashMap<>();
            eventWrapper.put("type", eventType);
            eventWrapper.put("orderId", aggregateId);
            eventWrapper.put("data", payloadData);
            String payload = objectMapper.writeValueAsString(eventWrapper);

            Outbox outbox = Outbox.builder()
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .type(eventType)
                    .payload(payload)
                    .build();

            outboxRepository.save(outbox);
            log.info("Outbox event saved: Type={}, ID={}", eventType, aggregateId);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize outbox payload", e);
            throw new BusinessException(OrderErrorCode.JSON_PROCESS_ERROR);
        }
    }
}
