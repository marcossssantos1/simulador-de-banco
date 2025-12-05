package com.banco.ms.dto;

import com.banco.ms.enums.StatusAccount;

import jakarta.validation.constraints.NotBlank;

public record ContaPfUpdateRequestDto (
		@NotBlank(message = "Nome é obrigatório para abrir uma conta") 
		String name, 
		StatusAccount status) {

}
