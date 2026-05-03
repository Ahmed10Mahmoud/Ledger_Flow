package com.ahmed.demo.application.service;
import com.ahmed.demo.application.dto.TransferResult;
import com.ahmed.demo.domain.command.LedgerEntryCommand;
import com.ahmed.demo.domain.command.TransactionCommand;
import com.ahmed.demo.domain.command.TransferCommand;
import com.ahmed.demo.domain.model.EntryType;
import com.ahmed.demo.infrastructure.persistence.Account;
import com.ahmed.demo.infrastructure.persistence.AccountRepository;
import com.ahmed.demo.infrastructure.persistence.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final AccountRepository accountRepository;
    private final LedgerEngine ledgerEngine;

    public TransferResult transfer(TransferCommand command) {

        int maxAttempts = 3;
        int attempt = 0;
        long delay = 100;

        while (true) {
            try {
                return doTransfer(command);
            } catch (jakarta.persistence.OptimisticLockException ex) {
                attempt++;

                if (attempt >= maxAttempts) {
                    throw ex;
                }

                sleep(delay);
                delay *= 2;
            }
        }
    }

    @org.springframework.transaction.annotation.Transactional(
            isolation = org.springframework.transaction.annotation.Isolation.SERIALIZABLE
    )
    protected TransferResult doTransfer(TransferCommand command) {

        // 🔥 Lock ordering (deadlock prevention)
        boolean fromFirst = command.fromAccountId()
                .compareTo(command.toAccountId()) < 0;

        Account first = accountRepository.findByIdWithLock(
                fromFirst ? command.fromAccountId() : command.toAccountId()
        ).orElseThrow(() -> new RuntimeException("Account not found"));

        Account second = accountRepository.findByIdWithLock(
                fromFirst ? command.toAccountId() : command.fromAccountId()
        ).orElseThrow(() -> new RuntimeException("Account not found"));

        Account from = fromFirst ? first : second;
        Account to = fromFirst ? second : first;

        // 1️⃣ Currency validation (safe after lock)
        if (!from.getCurrency().equals(command.currency()) ||
                !to.getCurrency().equals(command.currency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        // 🔥 2️⃣ Balance check INSIDE LOCK (FIXED)
        if (from.getBalance().compareTo(command.amount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // 3️⃣ Build entries
        LedgerEntryCommand debit = new LedgerEntryCommand(
                from.getId(),
                EntryType.DEBIT,
                command.amount(),
                command.description()
        );

        LedgerEntryCommand credit = new LedgerEntryCommand(
                to.getId(),
                EntryType.CREDIT,
                command.amount(),
                command.description()
        );

        TransactionCommand txCommand = new TransactionCommand(
                command.requestKey(),
                command.description(),
                List.of(debit, credit)
        );

        // 4️⃣ Execute transaction
        Transaction tx = ledgerEngine.postTransaction(txCommand);

        // 🔥 5️⃣ Return result (balances already updated)
        return new TransferResult(
                tx.getId(),
                tx.getReferenceKey(),
                from.getBalance(),
                to.getBalance(),
                command.amount(),
                java.time.Instant.now()
        );
    }

    private void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
