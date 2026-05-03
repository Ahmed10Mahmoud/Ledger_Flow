package com.ahmed.demo.application.service;
import com.ahmed.demo.domain.command.LedgerEntryCommand;
import com.ahmed.demo.domain.command.TransactionCommand;
import com.ahmed.demo.domain.model.EntryType;
import com.ahmed.demo.domain.model.TransactionStatus;
import com.ahmed.demo.domain.service.LedgerValidator;
import com.ahmed.demo.infrastructure.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LedgerEngine {

    private final LedgerValidator validator;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Transaction postTransaction(TransactionCommand command) {

        // 1️⃣ Validate
        validator.validate(command.entries());

        // 2️⃣ Create transaction
        Transaction transaction = Transaction.builder()
                .referenceKey(command.referenceKey())
                .description(command.description())
                .status(TransactionStatus.POSTED)
                .build();

        transactionRepository.save(transaction);

        // 3️⃣ Process entries
        List<LedgerEntry> entries = new ArrayList<>();

        for (LedgerEntryCommand cmd : command.entries()) {

            // 🔥 LOCK account
            Account account = accountRepository.findByIdWithLock(cmd.accountId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            // 4️⃣ Calculate current balance from DB
            BigDecimal currentBalance = ledgerEntryRepository
                    .calculateRunningBalance(account.getId())
                    .stream()
                    .reduce(BigDecimal.ZERO, (a, b) -> b); // last value

            if (currentBalance == null) {
                currentBalance = BigDecimal.ZERO;
            }

            // 5️⃣ Apply new entry
            BigDecimal newBalance = applyEntry(currentBalance, cmd);

            // 6️⃣ Update account (denormalized)
            account.setBalance(newBalance);

            // 7️⃣ Create ledger entry
            LedgerEntry entry = LedgerEntry.builder()
                    .account(account)
                    .transaction(transaction)
                    .type(cmd.type())
                    .amount(cmd.amount())
                    .runningBalance(newBalance)
                    .build();

            entries.add(entry);
        }

        // 🔥 Batch insert
        ledgerEntryRepository.saveAll(entries);

        return transaction;
    }

    private BigDecimal applyEntry(BigDecimal balance, LedgerEntryCommand cmd) {
        return cmd.type() == EntryType.DEBIT
                ? balance.subtract(cmd.amount())
                : balance.add(cmd.amount());
    }
}
