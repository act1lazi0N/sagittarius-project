package com.sagittarius.order.adapter.web;

import com.sagittarius.order.adapter.persistence.entity.OrderStatus;
import com.sagittarius.order.adapter.web.dto.CreateOrderRequest;
import com.sagittarius.order.adapter.web.dto.OrderResponse;
import com.sagittarius.order.application.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody @Valid CreateOrderRequest request)
    {
        return orderService.createOrder(request);
    }

    @GetMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrder(@PathVariable String orderNumber) {
        return orderService.getOrderByOrderNumber(orderNumber);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderResponse> searchOrders(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return orderService.searchOrders(customerId, email, status, pageable);
    }

    @PutMapping("/{orderNumber}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable String orderNumber) {
        orderService.cancelOrder(orderNumber);
    }

}
