package com.ahmed.demo.infrastructure.persistence;


import com.ahmed.demo.domain.model.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseEntity{
    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;
    @Column(nullable = false, length = 3)
    private String currency;
    @Enumerated(EnumType.STRING)
    @Column(name="account_type",nullable = false)
    private AccountType accountType;
    @Column(nullable = false)
    private String status;
    @Version
    private long version;
}
