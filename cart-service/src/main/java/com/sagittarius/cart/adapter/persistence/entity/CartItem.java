package com.sagittarius.cart.adapter.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String productId;
    private String skuCode;
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal price;
}