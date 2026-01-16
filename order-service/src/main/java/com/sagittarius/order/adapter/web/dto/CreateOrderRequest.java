package com.sagittarius.order.adapter.web.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
   @NotBlank(message = "Customer ID is required")
   private String customerId;

   @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
   private BigDecimal amount;

   @NotEmpty(message = "Order must have at least 1 item")
   private List<OrderItemRequest> items;

   @Data
   public static class OrderItemRequest {
      @NotBlank
      private String productId;
      @DecimalMin(value = "1")
      private int quantity;
      private BigDecimal price;
   }
}
