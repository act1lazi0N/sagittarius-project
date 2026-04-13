package com.sagittarius.order.infrastructure.client;

import com.sagittarius.order.application.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service")
@Component
public interface CartClient {
    @GetMapping("/api/carts/{customerId}")
    CartResponse getCart(@PathVariable("customerId") String customerId);

    @DeleteMapping("/api/carts/{customerId}/clear")
    void clearCart(@PathVariable("customerId") String customerId);
}
