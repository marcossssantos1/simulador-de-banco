package com.banco.ms.dto;

import java.math.BigDecimal;

import com.banco.ms.enums.TransferType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferRequestDto(
		@NotNull Long fromAccountId,
		@NotNull Long toAccountId,
		@NotNull @Positive BigDecimal amount,
		TransferType type,
		String pixKey,
		String description
		) {}
