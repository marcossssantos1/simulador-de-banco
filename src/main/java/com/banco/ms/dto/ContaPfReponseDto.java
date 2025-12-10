package com.banco.ms.dto;

import java.math.BigDecimal;
import java.util.List;

import com.banco.ms.enums.StatusAccount;

public record ContaPfReponseDto(
		String name, 
		BigDecimal balance, 
		StatusAccount status,
		String cpf,
		String agency,
		String numberAccount,
		List<PixKeyResponseDto> pixKeys
		) {}
