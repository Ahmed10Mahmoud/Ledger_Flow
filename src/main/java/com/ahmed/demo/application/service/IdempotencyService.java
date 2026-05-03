package com.ahmed.demo.application.service;


import com.ahmed.demo.domain.model.IdempotencyStatus;
import com.ahmed.demo.infrastructure.persistence.IdempotencyKey;
import com.ahmed.demo.infrastructure.persistence.IdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private final IdempotencyKeyRepository repository;

    public Optional<IdempotencyKey> check(UUID userId, String requestKey) {
        return repository.findByRequestKeyAndUserId(requestKey, userId);
    }
    public IdempotencyKey startProcessing(UUID userId, String requestKey) {

        IdempotencyKey key = IdempotencyKey.builder()
                .userId(userId)
                .requestKey(requestKey)
                .status(IdempotencyStatus.PROCESSING)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        return repository.save(key);
    }
    public void markCompleted(IdempotencyKey key, String response, int status) {
        key.setStatus(IdempotencyStatus.COMPLETED);
        key.setResponseBody(response);
        key.setHttpStatus(status);
        repository.save(key);
    }

    public void markFailed(IdempotencyKey key) {
        key.setStatus(IdempotencyStatus.FAILED);
        repository.save(key);
    }
}
