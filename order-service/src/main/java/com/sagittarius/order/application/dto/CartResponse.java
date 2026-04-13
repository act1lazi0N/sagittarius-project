package com.sagittarius.order.application.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CartResponse(String customerId,
                           List<CartItemResponse> items) {
    @Builder
    public record CartItemResponse(
            String productId,
            int quantity,
            BigDecimal price
    ) {
    }
}