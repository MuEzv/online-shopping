package com.project.accountservice.security;

import com.project.accountservice.service.AccountService;
import org.springframework.context.annotation.Bean;

public class OwnershipChecker {
    private final AccountService accountService;

    public OwnershipChecker(AccountService accountService) {
        System.out.println("OwnershipChecker bean created");

        this.accountService = accountService;
    }

    public boolean isOwnerEmail(String email, String authNameAccountId) {
        System.out.println("Checking ownership: email=" + email + ", authNameAccountId=" + authNameAccountId);
        if (email == null || authNameAccountId == null) {
            System.out.println("Email or authNameAccountId is null");
            return false;
        }
        String norm = email.trim().toLowerCase();
        boolean result = accountService.getAccountByEmail(norm)
                .map(acc -> {
                    boolean match = String.valueOf(acc.getId()).equals(authNameAccountId);
                    System.out.println("Account found: id=" + acc.getId() + ", match=" + match);
                    return match;
                })
                .orElse(false);
        System.out.println("Ownership check result: " + result);
        return result;
    }
}