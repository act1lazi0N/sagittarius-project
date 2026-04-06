package com.sagittarius.product.adapter.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    @Id
    private String id;

    @Indexed(unique = true)
    private String skuCode;

    @Indexed
    private String name;

    @Indexed(unique = true)
    private String slug;

    private String description;

    @Field("price")
    private BigDecimal price;

    @Field("compare_at_price")
    private BigDecimal compareAtPrice;

    @Indexed
    private String brand;

    @Indexed
    private List<String> categories;
    private List<String> images;
    private Map<String, Object> attributes;
    private ProductStatus status;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
