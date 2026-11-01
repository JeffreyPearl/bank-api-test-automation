   package com.bank.bankapp.model;

   import jakarta.validation.constraints.NotNull;
   import jakarta.validation.constraints.Positive;

   import java.math.BigDecimal;

   public record TransferRequest(
           @NotNull Long fromAccountId,
           @NotNull Long toAccountId,
           @NotNull @Positive BigDecimal amount) {
   }