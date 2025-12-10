package com.banco.ms.dto;

import com.banco.ms.enums.PixKeyType;

public record PixKeyResponseDto(
		Long id,
		String value,
		PixKeyType type
		) {

}
