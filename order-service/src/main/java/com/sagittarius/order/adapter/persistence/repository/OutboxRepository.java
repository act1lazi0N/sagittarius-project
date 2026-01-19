package com.sagittarius.order.adapter.persistence.repository;

import com.sagittarius.order.adapter.persistence.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
    List<Outbox> findTop50ByOrderByCreatedAtAsc();
}
