package com.ahmed.demo.application.service;

import com.ahmed.demo.domain.command.LedgerEntryCommand;
import com.ahmed.demo.domain.command.TransactionCommand;
import com.ahmed.demo.domain.model.EntryType;
import com.ahmed.demo.domain.model.TransactionStatus;
import com.ahmed.demo.infrastructure.persistence.LedgerEntry;
import com.ahmed.demo.infrastructure.persistence.Transaction;
import com.ahmed.demo.infrastructure.persistence.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReversalService {
    private final TransactionRepository transactionRepository;
    private final LedgerEngine ledgerEngine;

    @Transactional
    public Transaction reverse(UUID transactionId, UUID currentUserId, boolean isAdmin) {

        Transaction original = transactionRepository.findWithEntriesById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        validateReversal(original, currentUserId, isAdmin);

        List<LedgerEntryCommand> mirrorEntries =
                original.getEntries().stream()
                        .map(this::mirrorEntry)
                        .toList();

        TransactionCommand command = new TransactionCommand(
                "REV-" + original.getReferenceKey(),
                "Reversal of " + original.getReferenceKey(),
                mirrorEntries
        );

        Transaction reversal = ledgerEngine.postTransaction(command);

        reversal.setStatus(TransactionStatus.REVERSED);
        reversal.setReversalOf(original);

        // mark original reversed
        original.setStatus(TransactionStatus.REVERSED);

        transactionRepository.save(original);
        transactionRepository.save(reversal);

        return reversal;
    }

    private LedgerEntryCommand mirrorEntry(LedgerEntry entry) {

        EntryType swapped =
                entry.getType() == EntryType.DEBIT
                        ? EntryType.CREDIT
                        : EntryType.DEBIT;

        return new LedgerEntryCommand(
                entry.getAccount().getId(),
                swapped,
                entry.getAmount(),
                "Reversal"
        );
    }

    private void validateReversal(Transaction tx,
                                  UUID currentUserId,
                                  boolean isAdmin) {

        // already reversed
        if (tx.getStatus() == TransactionStatus.REVERSED ||
                transactionRepository.existsByReversalOfId(tx.getId())) {
            throw new IllegalStateException("Already reversed");
        }

        // failed tx cannot reverse
        if (tx.getStatus() == TransactionStatus.FAILED) {
            throw new IllegalStateException("Cannot reverse failed transaction");
        }

        // ownership unless admin
        if (!isAdmin && !tx.getCreatedBy().equals(currentUserId)) {
            throw new SecurityException("Cannot reverse another user's transaction");
        }
    }
}
