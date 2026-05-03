package com.ahmed.demo.infrastructure.persistence;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    Page<LedgerEntry> findByAccountIdOrderByCreatedAtDesc(UUID accountId, Pageable pageable);

    List<LedgerEntry> findByTransactionId(UUID transactionId);
    @Query(value = """
        SELECT SUM(amount) OVER (
            PARTITION BY account_id
            ORDER BY created_at
            ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
        )
        FROM ledger_entries
        WHERE account_id = :accountId
        """, nativeQuery = true)
    List<BigDecimal> calculateRunningBalance(@Param("accountId") UUID accountId);
}
