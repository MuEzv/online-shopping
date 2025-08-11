package com.project.accountservice.service;

import com.project.accountservice.entity.Account;
import com.project.accountservice.payload.AccountRequestDTO;
import com.project.accountservice.payload.AccountResponseDTO;

import java.util.Optional;


public interface AccountServiceImpl {
    AccountResponseDTO createAccount(AccountRequestDTO request);
    Optional<AccountResponseDTO> updateAccount(String email, AccountRequestDTO request);
    Optional<AccountResponseDTO>  getAccountByEmail(String email);
    Optional<Account> getAccountByEmailForLogin(String email);
}
