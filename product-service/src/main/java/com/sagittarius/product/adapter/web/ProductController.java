package com.sagittarius.product.adapter.web;

import com.sagittarius.product.adapter.persistence.entity.ProductDocument;
import com.sagittarius.product.adapter.persistence.entity.ProductEntity;
import com.sagittarius.product.application.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductEntity createProduct(@RequestBody ProductEntity product) {
        return productService.createProduct(product);
    }

    @GetMapping("/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public ProductEntity getProduct(@PathVariable String skuCode) {
        return productService.getProductBySku(skuCode);
    }

    @PutMapping("/{skuCode}")
    @ResponseStatus(value = HttpStatus.OK, reason = "The product has been updated!")
    public ProductEntity updateProduct(@PathVariable String skuCode, @RequestBody ProductEntity product) {
        return productService.updateProduct(skuCode, product);
    }

    @DeleteMapping("/{skuCode}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT, reason = "The product has been deleted!")
    public void deleteProduct(@PathVariable String skuCode) {
        productService.deleteProduct(skuCode);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDocument> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

}
