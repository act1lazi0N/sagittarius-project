package com.sagittarius.inventory.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_inventory")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku_code", nullable = false, unique = true)
    private String skuCode;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity = 0;

    @Column(name = "reorder_level")
    private Integer reorderLevel = 10;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;
    public Integer getAvailableStock() {
        return this.quantity - this.reservedQuantity;
    }
    public void reserveStock(int amount) {
        if (getAvailableStock() < amount) {
            throw new RuntimeException("OOS: Not enough stock for " + skuCode);
        }
        this.reservedQuantity += amount;
        this.lastUpdatedAt = LocalDateTime.now();
    }
    public void confirmSale(int amount) {
        this.reservedQuantity -= amount;
        this.quantity -= amount;
        this.lastUpdatedAt = LocalDateTime.now();
    }
    public void cancelReservation(int amount) {
        this.reservedQuantity -= amount;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }

}
