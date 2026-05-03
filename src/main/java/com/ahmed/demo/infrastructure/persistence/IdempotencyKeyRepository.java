package com.ahmed.demo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, UUID> {
    Optional<IdempotencyKey> findByRequestKeyAndUserId(String requestKey, UUID userId);

    void deleteByExpiresAtBefore(LocalDateTime time);
}
