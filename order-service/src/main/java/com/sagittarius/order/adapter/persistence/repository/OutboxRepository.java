package com.sagittarius.order.adapter.persistence.repository;

import com.sagittarius.order.adapter.persistence.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEntity,Long> {
    List<OutboxEntity> findTop50ByOrderByCreatedAtAsc();
}
