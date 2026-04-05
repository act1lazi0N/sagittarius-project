package com.sagittarius.cart.adapter.web.controller;

import com.sagittarius.cart.adapter.persistence.entity.Cart;
import com.sagittarius.cart.adapter.persistence.entity.CartItem;
import com.sagittarius.cart.application.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Cart getCart(@RequestHeader("X-User-Id") String customerId) {
        return cartService.getCart(customerId);
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public Cart addToCart(@RequestHeader("X-User-Id") String customerId,
                          @RequestBody CartItem cartItem) {
        return cartService.addToCart(customerId, cartItem);
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFromCart(@RequestHeader("X-User-Id") String customerId,
                              @PathVariable String productId) {
        cartService.removeFromCart(customerId, productId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@RequestHeader("X-User-Id") String customerId) {
        cartService.clearCart(customerId);
    }
}
