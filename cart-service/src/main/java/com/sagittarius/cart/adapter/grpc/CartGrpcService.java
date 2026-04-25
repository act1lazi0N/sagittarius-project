package com.sagittarius.cart.adapter.grpc;

import com.sagittarius.cart.application.service.CartService;
import com.sagittarius.grpc.cart.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class CartGrpcService extends CartServiceGrpc.CartServiceImplBase {
    private final CartService cartService;

    @Override
    public void getCart(GetCartRequest request, StreamObserver<CartResponse> responseObserver) {
        // Get cart
        var cart = cartService.getCart(request.getCustomerId());

        // Build response
        CartResponse.Builder responseBuilder = CartResponse.newBuilder()
                .setCustomerId(cart.getCustomerId());

        // Add items to response
        cart.getItems().forEach(item -> responseBuilder.addItems(CartItem.newBuilder()
                .setProductId(item.getProductId())
                .setQuantity(item.getQuantity())
                .setPrice(item.getPrice().toString())
                .build()));

        // Send response and close the connection
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void clearCart(ClearCartRequest request, StreamObserver<ClearCartResponse> responseObserver) {
        cartService.clearCart(request.getCustomerId());
        responseObserver.onNext(ClearCartResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }
}
