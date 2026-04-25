package com.sagittarius.order.infrastructure.client;

import com.sagittarius.grpc.cart.CartServiceGrpc;
import com.sagittarius.grpc.cart.ClearCartRequest;
import com.sagittarius.grpc.cart.GetCartRequest;
import com.sagittarius.order.application.dto.CartResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CartClient {
    @GrpcClient("cart-service")
    private CartServiceGrpc.CartServiceBlockingStub cartGrpcStub;

    public CartResponse getCart(String customerId) {
        var response = cartGrpcStub.getCart(GetCartRequest.newBuilder().setCustomerId(customerId).build());
        return new CartResponse(
                response.getCustomerId(),
                response.getItemsList().stream()
                        .map(item -> new CartResponse.CartItemResponse(item.getProductId(), item.getQuantity(), new BigDecimal(item.getPrice())))
                        .collect(Collectors.toList())
        );
    }
    public void clearCart(String customerId) {
        cartGrpcStub.clearCart(ClearCartRequest.newBuilder().setCustomerId(customerId).build());
    }
}
