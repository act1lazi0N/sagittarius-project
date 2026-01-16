package com.sagittarius.order.adapter.web;

import com.sagittarius.order.adapter.web.dto.CreateOrderRequest;
import com.sagittarius.order.application.service.CreateOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderService createOrderService;

    @PostMapping
    public ResponseEntity<UUID> createOrder(@RequestBody CreateOrderRequest request)
    {
        UUID orderId = createOrderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

}
