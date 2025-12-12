package com.banco.ms.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CardPurchaseDto(
		@NotNull Long cardId,
		@NotNull BigDecimal amount,
		@NotBlank String description
		) {

}
