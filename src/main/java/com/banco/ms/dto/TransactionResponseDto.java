package com.banco.ms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banco.ms.enums.TransactiontType;

public record TransactionResponseDto(
		Long id,
		BigDecimal amount,
		TransactiontType type,
		LocalDateTime date
		) {

}
