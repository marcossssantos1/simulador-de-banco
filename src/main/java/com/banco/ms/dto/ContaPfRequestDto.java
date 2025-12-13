package com.banco.ms.dto;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ContaPfRequestDto(
		@NotBlank(message = "Nome é obrigatório para abrir uma conta") 
        String name,
        
        @NotBlank(message = "CPF é obrigatório para abrir uma conta")
		@CPF
		@Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 números")
        String cpf,
        
        @NotNull(message = "Necessário informar a sua renda mensal para abrir uma conta")
        BigDecimal income
){}
