package com.banco.ms.dto;

import jakarta.validation.constraints.NotBlank;

public record CardRequestDto(
		@NotBlank String cpf
		) {}
