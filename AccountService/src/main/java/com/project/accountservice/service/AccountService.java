package com.project.accountservice.service;

import com.project.accountservice.entity.Account;
import com.project.accountservice.payload.AccountRequestDTO;
import com.project.accountservice.payload.AccountResponseDTO;
import com.project.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class AccountService implements AccountServiceImpl {
    @Autowired
    private  AccountRepository repository;
    @Autowired
    private  PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountResponseDTO createAccount(AccountRequestDTO request) {
        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setUsername(request.getUsername());
        account.setShippingAddress(request.getShippingAddress());
        account.setBillingAddress(request.getBillingAddress());
        account.setPaymentMethod(request.getPaymentMethod());
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        Account saved = repository.save(account);
        return mapToResponse(saved);
    }

    @Override
    public Optional<AccountResponseDTO> updateAccount(String email, AccountRequestDTO request) {
        return repository.findByEmail(email).map(account -> {
            account.setUsername(request.getUsername());
            account.setShippingAddress(request.getShippingAddress());
            account.setBillingAddress(request.getBillingAddress());
            account.setPaymentMethod(request.getPaymentMethod());
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                account.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            Account updated = repository.save(account);
            return mapToResponse(updated);
        });
    }

    public Optional<AccountResponseDTO> getAccountByEmail(String email) {
        return repository.findByEmail(email).map(this::mapToResponse);
    }

    @Override
    public Optional<Account> getAccountByEmailForLogin(String email) {
        return repository.findEntityByEmail(email);
    }

    private AccountResponseDTO mapToResponse(Account acc) {
        return new AccountResponseDTO(
                acc.getId(),
                acc.getEmail(),
                acc.getUsername(),
                acc.getShippingAddress(),
                acc.getBillingAddress(),
                acc.getPaymentMethod()
        );
    }


}
