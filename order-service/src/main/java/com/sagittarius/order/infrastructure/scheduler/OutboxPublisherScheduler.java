package com.sagittarius.order.infrastructure.scheduler;

import com.sagittarius.order.adapter.persistence.entity.OutboxEntity;
import com.sagittarius.order.adapter.persistence.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisherScheduler {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 2000)
    public void publishOutboxEvents() {
        List<OutboxEntity> events = outboxRepository.findTop50ByOrderByCreatedAtAsc();

        if (events.isEmpty()) {
            return;
        }

        log.debug("Found {} events in outbox. Starting processing...", events.size());

        for (OutboxEntity event : events) {
            try {
                String topic = event.getAggregateType().toLowerCase() + "-events";
                String key = event.getAggregateId();
                String payload = event.getPayload();

                kafkaTemplate.send(topic, key, payload)
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                outboxRepository.delete(event);
                                log.info("Event sent to Kafka and deleted from DB. ID={}", event.getId());
                            } else {
                                log.error("Failed to send event ID={}", event.getId(), ex);
                            }
                        });

            } catch (Exception e) {
                log.error("Unhandled exception for event ID={}", event.getId(), e);
            }
        }
    }
}

