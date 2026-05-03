package com.ahmed.demo.application.security;

import com.ahmed.demo.infrastructure.persistence.AccountRepository;
import com.ahmed.demo.infrastructure.persistence.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountSecurity {
    private final AccountRepository accountRepository;
    public boolean isOwner(UUID accountId, Authentication auth) {

        UserEntity user = (UserEntity) auth.getPrincipal();

        return accountRepository.findById(accountId)
                .map(a -> a.getOwnerId().equals(user.getId()))
                .orElse(false);
    }
}
