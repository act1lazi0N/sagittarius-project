package com.sagittarius.order.infrastructure.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ProductClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal getProductPrice(String skuCode) {
        String url = "http://localhost:8086/api/v1/products/" + skuCode;
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.get("price") != null) {
                return new BigDecimal(response.get("price").toString());
            }
            throw new RuntimeException("Unavailable price for product: " + skuCode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to Product Service to get price from product: " + skuCode, e);
        }
    }
}
