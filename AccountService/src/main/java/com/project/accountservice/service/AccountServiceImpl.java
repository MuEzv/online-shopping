package com.project.accountservice.service;

import com.project.accountservice.payload.AccountRequestDTO;
import com.project.accountservice.payload.AccountResponseDTO;

import java.util.Optional;


public interface AccountServiceImpl {
    AccountResponseDTO createAccount(AccountRequestDTO request);
    Optional<AccountResponseDTO> updateAccount(String email, AccountRequestDTO request);
    Optional<AccountResponseDTO>  getAccountByEmail(String email);
    Optional<AccountResponseDTO> login(String email, String password);}
