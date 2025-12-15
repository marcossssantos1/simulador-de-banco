package com.banco.ms.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InvoicePaymentRequestDto (
		@NotNull
		@Positive
		BigDecimal amount
		){

}
