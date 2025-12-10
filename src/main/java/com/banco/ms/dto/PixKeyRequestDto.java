package com.banco.ms.dto;

import com.banco.ms.enums.PixKeyType;

import jakarta.validation.constraints.NotNull;

public record PixKeyRequestDto(

		String pixKey,
		
		@NotNull(message = "O tipo da chave PIX é obrigatório")
		PixKeyType pixType
		) {}
