package com.sagittarius.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/order")
    public Mono<String> orderFallback() {
        return Mono.just("Order Service đang gặp sự cố hoặc quá tải. Vui lòng thử lại sau ít phút!");
    }

    @RequestMapping("/fallback/inventory")
    public Mono<String> inventoryFallback() {
        return Mono.just("Inventory Service đang bảo trì. Không thể kiểm tra kho hàng lúc này.");
    }

    @RequestMapping("/fallback/payment")
    public Mono<String> paymentFallback() {
        return Mono.just("Hệ thống thanh toán (Payment) đang gián đoạn. Xin lỗi vì sự bất tiện này.");
    }
}
