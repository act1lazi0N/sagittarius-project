package com.sagittarius.payment.adapter.persistence.repository;

import com.sagittarius.payment.adapter.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
}
