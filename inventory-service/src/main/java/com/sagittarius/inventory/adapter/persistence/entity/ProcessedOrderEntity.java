package com.sagittarius.inventory.adapter.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_processed_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedOrderEntity {
    @Id
    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ProcessedOrderEntity(String orderNumber) {
        this.orderNumber = orderNumber;
        this.createdAt = LocalDateTime.now();
    }
}
