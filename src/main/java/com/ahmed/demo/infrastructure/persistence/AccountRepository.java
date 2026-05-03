package com.ahmed.demo.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface  AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByOwnerIdAndCurrency(UUID ownerId,String curreny);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithLock(@Param("id") UUID id);
    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.ownerId = :ownerId")
    BigDecimal sumBalanceByOwner(@Param("ownerId") UUID ownerId);

}
