package com.ahmed.demo.domain.command;

import com.ahmed.demo.domain.model.EntryType;

import java.math.BigDecimal;
import java.util.UUID;

public record LedgerEntryCommand(
        UUID accountId,
        EntryType type,     // DEBIT / CREDIT
        BigDecimal amount,
        String description) {}
