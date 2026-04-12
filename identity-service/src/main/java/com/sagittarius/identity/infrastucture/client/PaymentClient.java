package com.sagittarius.identity.infrastucture.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PAYMENT-SERVICE", path = "/api/v1/balances")
public interface PaymentClient {
    @PostMapping("/open")
    void openWallet(@RequestParam("customerId") String customerId);
}
