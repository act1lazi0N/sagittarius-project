package com.sagittarius.product.application.service;

import com.sagittarius.common.exception.ResourceNotFoundException;
import com.sagittarius.product.adapter.persistence.entity.ProductDocument;
import com.sagittarius.product.adapter.persistence.entity.ProductEntity;
import com.sagittarius.product.adapter.persistence.repository.ProductRepository;
import com.sagittarius.product.adapter.persistence.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductSearchRepository searchRepository;

    @Cacheable(value = "products_detail", key = "#skuCode")
    public ProductEntity getProductBySku(String skuCode) {
        log.info("Cache miss! Fetching product '{}' directly from MongoDB.", skuCode);

        return productRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + skuCode));
    }

    @Transactional
    public ProductEntity createProduct(ProductEntity product) {
        ProductEntity savedProduct = productRepository.save(product);
        syncToElasticsearch(savedProduct);
        log.info("Product '{}' created successfully.", product.getSkuCode());
        return savedProduct;
    }

    @Transactional
    @CacheEvict(value = "products_detail", key="#skuCode")
    public ProductEntity updateProduct(String skuCode, ProductEntity request) {
        ProductEntity existingProduct = getProductBySku(skuCode);

        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setCategories(request.getCategories());

        ProductEntity updatedProduct = productRepository.save(existingProduct);
        syncToElasticsearch(updatedProduct);

        log.info("Product updated and cache evicted: {}", skuCode);
        return updatedProduct;
    }

    @Transactional
    @CacheEvict(value = "products_detail", key="#skuCode")
    public void deleteProduct(String skuCode) {
        ProductEntity existingProduct = getProductBySku(skuCode);
        productRepository.delete(existingProduct);
        searchRepository.deleteById(skuCode);
        log.info("Product deleted: {}", skuCode);
    }

    public List<ProductDocument> searchProducts(String keyword) {
        log.info("Searching products in ElasticSearch with keyword: {}", keyword);
        return searchRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);
    }

    private void syncToElasticsearch(ProductEntity product) {
        searchRepository.save(ProductDocument.builder()
                .skuCode(product.getSkuCode())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .brand(product.getBrand())
                .categories(product.getCategories())
                .build());
    }
}
