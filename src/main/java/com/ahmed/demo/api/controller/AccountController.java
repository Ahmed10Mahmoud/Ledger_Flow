package com.ahmed.demo.api.controller;


import com.ahmed.demo.application.dto.AccountSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountSecurity.isOwner(#id, authentication)")
    public AccountSummaryDTO getAccount(@PathVariable UUID id) {
        return accountService.getById(id);
    }
}
