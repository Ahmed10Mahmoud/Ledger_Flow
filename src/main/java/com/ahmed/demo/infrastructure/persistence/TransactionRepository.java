package com.ahmed.demo.infrastructure.persistence;

import com.ahmed.demo.domain.model.TransactionStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByReferenceKeyAndCreatedBy(String referenceKey, UUID createdBy);

    List<Transaction> findByStatusAndCreatedAtBefore(
            TransactionStatus status,
            Instant before
    );

    @EntityGraph(attributePaths = {"entries"})
    Optional<Transaction> findWithEntriesById(UUID id);

    boolean existsByReversalOfId(UUID originalId);

}
