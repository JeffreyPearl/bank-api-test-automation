package com.bank.bankapp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank String holderName,
        @NotNull @PositiveOrZero BigDecimal initialDeposit) {
}