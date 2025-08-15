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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.accountservice.entity.Role;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/account/auth")
public class AuthController {
    @Autowired
    private AccountService service;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        Optional<Account> userOpt = service.getAccountByEmailForLogin(request.getEmail());

        if (userOpt.isPresent() && passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            Account acc = userOpt.get();

            Set<Role> roles = acc.getRoles() == null ? Collections.emptySet() : acc.getRoles();
            String[] roleNames;
            if (roles.isEmpty()) {
                roleNames = new String[] {"USER"};
            } else {
                roleNames = roles.stream()
                        .map(Role::getName)
                        .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                        .toArray(String[]::new);
            }
            String token = JwtUtil.generateTokenSubjectIsAccountId(
                    String.valueOf(acc.getId()),   // sub = accountId
                    acc.getEmail(),                // 附加的 claim
                    roleNames                      // 从数据库取的角色
            );

            return ResponseEntity.ok(new JwtResponseDTO(token, "Bearer", 3600));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
    }

    @PostMapping("/register")
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody AccountRequestDTO request) {
        return ResponseEntity.ok(service.createAccount(request));
    }
}
