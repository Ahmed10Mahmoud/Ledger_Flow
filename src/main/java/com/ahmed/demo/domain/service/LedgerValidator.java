package com.ahmed.demo.domain.service;

import com.ahmed.demo.domain.command.LedgerEntryCommand;
import com.ahmed.demo.domain.model.EntryType;
import com.ahmed.demo.exception.LedgerImbalanceException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LedgerValidator {

    public void validate(List<LedgerEntryCommand> entries) {
        validateMinimumEntries(entries);
        validatePositiveAmounts(entries);
        validateNoDuplicateAccounts(entries);
        validateZeroSum(entries);
    }

    public void validatePositiveAmounts(List<LedgerEntryCommand> entries) {
        for (LedgerEntryCommand entry : entries) {
            if (entry.amount() == null || entry.amount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
        }
    }
    public void validateMinimumEntries(List<LedgerEntryCommand> entries) {
        if (entries == null || entries.size() < 2) {
            throw new IllegalArgumentException("Transaction must have at least 2 entries");
        }
    }
    public void validateNoDuplicateAccounts(List<LedgerEntryCommand> entries) {
        Set<UUID> accounts = new HashSet<>();
        for (LedgerEntryCommand entry : entries) {
            if (!accounts.add(entry.accountId())) {
                throw new IllegalArgumentException("Duplicate account in transaction");
            }
        }
    }
    public void validateZeroSum(List<LedgerEntryCommand> entries) {
        BigDecimal total = BigDecimal.ZERO;

        for (LedgerEntryCommand entry : entries) {
            BigDecimal signed = entry.type() == EntryType.DEBIT
                    ? entry.amount().negate()
                    : entry.amount();

            total = total.add(signed);
        }

        if (total.compareTo(BigDecimal.ZERO) != 0) {
            throw new LedgerImbalanceException("Ledger entries are not balanced");
        }
    }

}
