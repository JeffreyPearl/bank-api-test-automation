package com.bank.bankapp.service;

import com.bank.bankapp.model.Account;
import com.bank.bankapp.model.CreateAccountRequest;
import com.bank.bankapp.model.TransferResponse;
import com.bank.bankapp.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(CreateAccountRequest request) {
        Account account = new Account(request.holderName(), request.initialDeposit());
        return accountRepository.save(account);
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account not found: " + id));
    }

    @Transactional
    public Account deposit(Long id, BigDecimal amount) {
        Account account = getAccount(id);
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(Long id, BigDecimal amount) {
        Account account = getAccount(id);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Insufficient funds in account " + id);
        }
        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public TransferResponse transfer(Long fromId, Long toId, BigDecimal amount) {
        if (fromId.equals(toId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cannot transfer to the same account");
        }
        Account from = withdraw(fromId, amount);
        Account to = deposit(toId, amount);
        return new TransferResponse(from, to);
    }
}