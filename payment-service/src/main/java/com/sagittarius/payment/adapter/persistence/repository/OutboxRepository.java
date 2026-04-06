package com.sagittarius.payment.adapter.persistence.repository;

import com.sagittarius.payment.adapter.persistence.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEntity, String> {
    @Query(value = "SELECT * FROM t_outbox_events ORDER BY created_at ASC LIMIT 50 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<OutboxEntity> findTop50ByOrderByCreatedAtAsc();
}
