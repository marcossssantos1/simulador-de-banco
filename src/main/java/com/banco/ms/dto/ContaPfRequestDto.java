package com.banco.ms.dto;

import jakarta.validation.constraints.NotBlank;

public record ContaPfRequestDto(
		@NotBlank(message = "Nome é obrigatório para abrir uma conta") 
        String name
){

}
