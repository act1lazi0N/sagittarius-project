package com.sagittarius.order.adapter.persistence.repository;

import com.sagittarius.order.adapter.persistence.entity.Order;
import com.sagittarius.order.adapter.persistence.entity.OrderStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class OrderSpecification {
    public static Specification<Order> hasCustomerId(String customerId) {
        return ((root, query, criteriaBuilder) -> {
            if(!StringUtils.hasText(customerId)) return null;
            return criteriaBuilder.equal(root.get("customerId"), customerId);
        } );
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return ((root, query, criteriaBuilder) -> {
            if(status == null) return null;
            return criteriaBuilder.equal(root.get("status"), status);
        } );
    }

    public static Specification<Order> containsEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(email)) return null;
            return criteriaBuilder.like(root.get("email"), "%" + email + "%");
        };
    }
}
