package com.sagittarius.inventory.adapter.persistence.repository;

import com.sagittarius.inventory.adapter.persistence.entity.ProcessedOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrderEntity, String> {
}
