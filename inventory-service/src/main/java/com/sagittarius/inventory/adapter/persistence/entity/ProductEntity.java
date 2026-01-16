package com.sagittarius.inventory.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "products", schema = "inventory_service")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;
}
