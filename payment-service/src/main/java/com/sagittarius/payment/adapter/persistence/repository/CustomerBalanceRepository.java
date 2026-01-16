package com.sagittarius.payment.adapter.persistence.repository;

import com.sagittarius.payment.adapter.persistence.entity.CustomerBalanceEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerBalanceRepository extends JpaRepository<CustomerBalanceEntity, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CustomerBalanceEntity c WHERE c.customerId = :id")
    Optional<CustomerBalanceEntity> findByIdAndLock(String id);
}
