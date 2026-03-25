package com.sagittarius.payment.adapter.web;

import com.sagittarius.payment.application.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping("/open")
    @ResponseStatus(value = HttpStatus.CREATED, reason = "Successfully created wallet!")
    public String openWallet(@RequestParam String customerId) {
        return balanceService.openWallet(customerId);
    }
}
