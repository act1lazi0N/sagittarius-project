package com.sagittarius.product.adapter.grpc;

import com.sagittarius.grpc.product.GetProductPriceRequest;
import com.sagittarius.grpc.product.GetProductPriceResponse;
import com.sagittarius.grpc.product.ProductServiceGrpc;
import com.sagittarius.product.application.service.ProductService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {
    private final ProductService productService;

    @Override
    public void getProductPrice(GetProductPriceRequest request, StreamObserver<GetProductPriceResponse> responseObserver) {
        // Get the product price from the product service
        String skuCode = request.getSkuCode();
        String price = productService.getProductBySku(skuCode).getPrice().toString();

        // Create a response object
        GetProductPriceResponse response = GetProductPriceResponse.newBuilder().setPrice(price).build();

        // Send the response back to the client and close the stream
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
