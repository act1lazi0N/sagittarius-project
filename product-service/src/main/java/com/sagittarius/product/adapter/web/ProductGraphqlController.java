package com.sagittarius.product.adapter.web;

import com.sagittarius.product.application.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ProductGraphqlController {
    private final ProductService productService;

    @QueryMapping
    public Object getProductBySku(@Argument String skuCode) {
        return productService.getProductBySku(skuCode);
    }

    @QueryMapping
    public Object searchProducts(@Argument String keyword) {
        return productService.searchProducts(keyword);
    }
}
