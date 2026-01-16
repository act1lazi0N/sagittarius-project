package com.sagittarius.inventory.adapter.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_orders", schema = "inventory_service")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedOrderEntity {
    @Id
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
