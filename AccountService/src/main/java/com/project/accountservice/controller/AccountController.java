package com.project.accountservice.controller;

import com.project.accountservice.entity.Account;
import com.project.accountservice.payload.AccountRequestDTO;
import com.project.accountservice.payload.AccountResponseDTO;
import com.project.accountservice.payload.JwtResponseDTO;
import com.project.accountservice.payload.LoginRequestDTO;
import com.project.accountservice.service.AccountService;
import com.project.accountservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService service;

    @PutMapping("/{email}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable String email,
            @RequestBody AccountRequestDTO request) {
        return service.updateAccount(email, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{email}")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable String email) {
        return service.getAccountByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/test/{email}")
    @PreAuthorize("@ownershipChecker.isOwnerEmail(#email, authentication.name) or hasRole('ADMIN')")
    public String getEmail(@PathVariable String email) {
        return email;
    }
}
