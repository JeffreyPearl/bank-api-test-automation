package com.bank.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.bank.bankapp.model.Account;
import com.bank.bankapp.model.CreateAccountRequest;
import com.bank.bankapp.model.TransferResponse;
import com.bank.bankapp.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account account(String name, String balance) {
        return new Account(name, new BigDecimal(balance));
    }

    @Test
    void createAccountSavesAndReturnsAccount() {
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account created = accountService.createAccount(
                new CreateAccountRequest("Priya", new BigDecimal("1000")));

        assertEquals("Priya", created.getHolderName());
        assertEquals(new BigDecimal("1000"), created.getBalance());
    }

    @Test
    void getAccountThrows404WhenMissing() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> accountService.getAccount(99L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void depositIncreasesBalance() {
        Account acc = account("A", "1000");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountService.deposit(1L, new BigDecimal("500"));

        assertEquals(new BigDecimal("1500"), result.getBalance());
    }

    @Test
    void withdrawReducesBalance() {
        Account acc = account("A", "1000");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountService.withdraw(1L, new BigDecimal("400"));

        assertEquals(new BigDecimal("600"), result.getBalance());
    }

    @Test
    void withdrawMoreThanBalanceThrows409AndDoesNotSave() {
        Account acc = account("A", "100");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> accountService.withdraw(1L, new BigDecimal("5000")));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void withdrawExactBalanceIsAllowed() {
        Account acc = account("A", "100");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountService.withdraw(1L, new BigDecimal("100"));

        assertEquals(0, result.getBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    void transferMovesMoneyBetweenAccounts() {
        Account from = account("Sender", "5000");
        Account to = account("Receiver", "1000");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(to));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        TransferResponse result = accountService.transfer(1L, 2L, new BigDecimal("1000"));

        assertEquals(new BigDecimal("4000"), result.fromAccount().getBalance());
        assertEquals(new BigDecimal("2000"), result.toAccount().getBalance());
    }

    @Test
    void transferToSameAccountThrows400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> accountService.transfer(1L, 1L, new BigDecimal("100")));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void transferWithInsufficientFundsThrows409() {
        Account from = account("Sender", "50");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(from));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> accountService.transfer(1L, 2L, new BigDecimal("999")));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }
}