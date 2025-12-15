package com.banco.ms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banco.ms.model.Purchase;

public record PurchaseResponseDto (
		Long id,
		BigDecimal amount,
		String description,
		LocalDateTime date
		){
	
	public static PurchaseResponseDto from(Purchase p) {
		return new PurchaseResponseDto(p.getId(), p.getAmount(), p.getDescription(), p.getDate());
	}
}
