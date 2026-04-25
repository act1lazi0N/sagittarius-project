package com.sagittarius.order.infrastructure.client;

import com.sagittarius.common.exception.BusinessException;
import com.sagittarius.grpc.product.GetProductPriceRequest;
import com.sagittarius.grpc.product.GetProductPriceResponse;
import com.sagittarius.grpc.product.ProductServiceGrpc;
import com.sagittarius.order.application.exception.OrderErrorCode;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Slf4j
public class ProductClient {

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub productGrpcStub;

    public BigDecimal getProductPrice(String skuCode) {

        try {
            // Get the product price from the product service
            GetProductPriceRequest request = GetProductPriceRequest.newBuilder()
                    .setSkuCode(skuCode)
                    .build();

            // Get request from product service
            GetProductPriceResponse response = productGrpcStub.getProductPrice(request);

            // Return the price
            return new BigDecimal(response.getPrice());
        } catch (Exception e) {
            log.error("Lỗi CHI TIẾT khi gọi Product Service: {}", e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.PRODUCT_PRICE_UNAVAILABLE);
        }
    }
}
