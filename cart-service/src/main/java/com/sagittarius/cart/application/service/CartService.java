package com.sagittarius.cart.application.service;

import com.sagittarius.cart.adapter.persistence.entity.Cart;
import com.sagittarius.cart.adapter.persistence.entity.CartItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CART_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofDays(7); // 7 days before cancel

    public Cart getCart(String customerId) {
        String key = CART_PREFIX + customerId;
        Cart cart = (Cart) redisTemplate.opsForValue().get(key);

        if (cart == null) {
            cart = Cart.builder().customerId(customerId).build();
        }

        return cart;
    }

    public Cart addToCart(String customerId, CartItem newItem) {
        Cart cart = getCart(customerId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(newItem.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + newItem.getQuantity());
            item.setPrice(newItem.getPrice());
        } else {
            cart.getItems().add(newItem);
        }
        return saveCart(cart);
    }

    public Cart removeFromCart(String customerId, String productId) {
        Cart cart = getCart(customerId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        return saveCart(cart);
    }

    public void clearCart(String customerId) {
        String key = CART_PREFIX + customerId;
        redisTemplate.delete(key);
        log.info("Cleared cart for customer: {}", customerId);
    }

    private Cart saveCart(Cart cart) {
        String key = CART_PREFIX + cart.getCustomerId();
        redisTemplate.opsForValue().set(key, cart, CART_TTL);
        log.info("Saved cart for customer: {}", cart.getCustomerId());
        return cart;
    }

}
