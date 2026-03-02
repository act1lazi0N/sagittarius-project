package com.sagittarius.inventory.adapter.persistence.repository;

import com.sagittarius.inventory.adapter.persistence.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, String> {
    List<Outbox> findTop50ByOrderByCreatedAtAsc();
}
