package com.sagittarius.product.application.service;

import com.sagittarius.common.exception.ResourceNotFoundException;
import com.sagittarius.product.adapter.persistence.entity.ProductEntity;
import com.sagittarius.product.adapter.persistence.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Cacheable(value = "products_detail", key = "#skuCode")
    public ProductEntity getProductBySku(String skuCode) {
        log.info("CACHE MISS! Fetching product '{}' directly from MongoDB.", skuCode);

        return productRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + skuCode));
    }
}
