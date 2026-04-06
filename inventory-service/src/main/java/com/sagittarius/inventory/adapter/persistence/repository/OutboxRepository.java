package com.sagittarius.inventory.adapter.persistence.repository;

import com.sagittarius.inventory.adapter.persistence.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, String> {
    @Query(value = "SELECT * FROM t_outbox_events ORDER BY created_at ASC LIMIT 50 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<Outbox> findTop50ByOrderByCreatedAtAsc();
}
