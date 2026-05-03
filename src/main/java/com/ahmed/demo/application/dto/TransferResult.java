package com.ahmed.demo.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferResult(
        UUID transactionId,
        String referenceKey,
        BigDecimal fromBalance,
        BigDecimal toBalance,
        BigDecimal amount,
        Instant timestamp
) {
}
