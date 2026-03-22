package com.sagittarius.product.adapter.web;

import com.sagittarius.product.adapter.persistence.entity.ProductEntity;
import com.sagittarius.product.application.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{skuCode}")
    @ResponseStatus(value = HttpStatus.OK, reason = "The product has been found!")
    public ProductEntity getProduct(@PathVariable String skuCode) {
        return productService.getProductBySku(skuCode);
    }
}
