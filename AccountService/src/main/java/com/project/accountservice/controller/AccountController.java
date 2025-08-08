package com.project.accountservice.controller;

import com.project.accountservice.payload.AccountRequestDTO;
import com.project.accountservice.payload.AccountResponseDTO;
import com.project.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService service;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody AccountRequestDTO request) {
        return ResponseEntity.ok(service.createAccount(request));
    }

    @GetMapping("/{email}")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable String email) {
        return service.getAccountByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
