package com.ahmed.demo.domain.command;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferCommand(
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal amount,
        String currency,
        String description,
        String requestKey
) {
}
