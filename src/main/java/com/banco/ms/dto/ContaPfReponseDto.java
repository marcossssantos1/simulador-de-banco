package com.banco.ms.dto;

import java.math.BigDecimal;

import com.banco.ms.enums.StatusAccount;

public record ContaPfReponseDto(String name, BigDecimal balance, StatusAccount status) {
	
	

}
