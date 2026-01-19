package com.sagittarius.order.adapter.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
   @NotBlank(message = "Customer ID không được để trống")
   private String customerId;

   @NotBlank(message = "Email không được để trống")
   @Email(message = "Email không đúng định dạng")
   private String email;

   @NotBlank(message = "Địa chỉ giao hàng không được để trống")
   private String shippingAddress;

   @NotNull(message = "Tổng tiền không được để trống")
   @Positive(message = "Tổng tiền phải lớn hơn 0")
   private BigDecimal amount;

   @NotEmpty(message = "Giỏ hàng không được để trống")
   @Valid
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
