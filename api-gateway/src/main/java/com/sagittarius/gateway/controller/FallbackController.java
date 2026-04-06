package com.sagittarius.gateway.controller;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/order")
    public Mono<ResponseEntity<Map<String, Object>>> orderFallback() {
        return buildErrorResponse("Order Service đang quá tải. Vui lòng thử lại sau!");
    }

    @RequestMapping("/fallback/inventory")
    public Mono<ResponseEntity<Map<String, Object>>> inventoryFallback() {
        return buildErrorResponse("Inventory Service đang quá tải. Vui lòng thử lại sau!");
    }

    @RequestMapping("/fallback/payment")
    public Mono<ResponseEntity<Map<String, Object>>> paymentFallback() {
        return buildErrorResponse("Payment Service đang quá tải. Vui lòng thử lại sau!");
    }

    private Mono<ResponseEntity<Map<String, Object>>> buildErrorResponse(String message) {
        Map<String, Object> error = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "error", HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                "message", message
        );
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error));
    }


}
