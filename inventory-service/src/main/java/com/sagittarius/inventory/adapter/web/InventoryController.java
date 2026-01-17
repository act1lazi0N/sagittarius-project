package com.sagittarius.inventory.adapter.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String addToStock() {
        return "Xin chào Admin! Đã nhập kho thành công (Mô phỏng).";
    }
}
