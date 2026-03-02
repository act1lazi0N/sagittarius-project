package com.sagittarius.payment.adapter.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "t_customer_balances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBalanceEntity {
    @Id
    @Column(name = "customer_id")
    private String customerId;

    @Column(nullable = false)
    private BigDecimal balance;
}
