package com.sagittarius.order.adapter.persistence.repository;

import com.sagittarius.order.adapter.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}
