package com.banco.ms.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferRequestDto(
		@NotNull Long fromAccountId,
		@NotNull Long toAccountId,
		@NotNull @Positive BigDecimal amount
		) {}
