package com.ahmed.demo.infrastructure.schedualing;


import com.ahmed.demo.infrastructure.persistence.IdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class IdempotencyCleanupTask {
    private final IdempotencyKeyRepository repository;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanup() {
        repository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
