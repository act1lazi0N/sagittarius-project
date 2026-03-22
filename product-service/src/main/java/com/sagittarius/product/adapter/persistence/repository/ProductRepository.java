package com.sagittarius.product.adapter.persistence.repository;

import com.sagittarius.product.adapter.persistence.entity.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<ProductEntity, String> {
    Optional<ProductEntity> findBySkuCode(String skuCode);
}
