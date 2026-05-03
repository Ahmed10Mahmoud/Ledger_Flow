package com.ahmed.demo.domain.command;

import java.util.List;

public record TransactionCommand(
        String referenceKey,
        String description,
        List<LedgerEntryCommand> entries
) {
}
