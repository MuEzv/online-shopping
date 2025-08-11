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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService service;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody AccountRequestDTO request) {
        return ResponseEntity.ok(service.createAccount(request));
    }

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        Optional<Account> userOpt = service.getAccountByEmailForLogin(request.getEmail());
        System.out.println("user found? " + userOpt.isPresent());
        if (userOpt.isPresent() && passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            String token = JwtUtil.generateToken(userOpt.get().getEmail());
            System.out.println("user found? " + userOpt.isPresent());
            System.out.println("matches? " + (userOpt.isPresent() && passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())));
            return ResponseEntity.ok(new JwtResponseDTO(token, "Bearer", 900));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
