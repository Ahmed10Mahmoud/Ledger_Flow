package com.ahmed.demo.infrastructure.persistence;

import com.ahmed.demo.domain.model.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(
        name = "transactions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_tx_reference_per_user", columnNames = {"reference_key", "created_by"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity{
    @Column(name = "reference_key", nullable = false)
    private String referenceKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    @Column(nullable = false, length = 3)
    private String currency;

    private String description;

    @OneToMany(mappedBy ="transaction",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LedgerEntry> entries;
    // reversal transaction points to original
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reversal_of_id")
    private Transaction reversalOf;

}
