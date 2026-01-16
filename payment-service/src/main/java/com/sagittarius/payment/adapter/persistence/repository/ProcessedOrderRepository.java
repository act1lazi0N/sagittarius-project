package com.sagittarius.payment.adapter.persistence.repository;


import com.sagittarius.payment.adapter.persistence.entity.ProcessedOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrderEntity, String> {
}
