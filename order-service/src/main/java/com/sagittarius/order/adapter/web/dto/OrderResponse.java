package com.sagittarius.order.adapter.web.dto;

import com.sagittarius.order.adapter.persistence.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private String orderNumber;
    private String customerId;
    private String email;
    private String shippingAddress;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    @Data
    @Builder
    public static class OrderItemResponse {
        private String skuCode;
        private BigDecimal price;
        private Integer quantity;
    }
}
