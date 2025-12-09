package com.banco.ms.dto;

import jakarta.validation.constraints.NotBlank;

public record PixKeyRequestDto(
		@NotBlank(message = "A chave PIX é obrigatória.")
		String pixKey
		) {

}
