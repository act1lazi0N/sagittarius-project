package com.sagittarius.payment.adapter.persistence.repository;

import com.sagittarius.payment.adapter.persistence.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEntity, String> {
    List<OutboxEntity> findTop50ByOrderByCreatedAtAsc();
}
