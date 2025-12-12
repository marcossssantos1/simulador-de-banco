package com.banco.ms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.banco.ms.enums.CardStatus;
import com.banco.ms.enums.CardTier;
import com.banco.ms.model.Card;

public record CarResponseDto(
		String cardNumber,
		CardTier tier,
		BigDecimal limitValue,
		BigDecimal usedLimit,
		CardStatus status,
		LocalDate expiraiton
		) {
	
	public CarResponseDto(Card card) {
		this(
				card.getCardNumber(),
				card.getCardTier(),
				card.getLimitValue(),
				card.getUsedlimit(),
				card.getCardStatus(),
				card.getExpiration()
				);
	}

	
	
	
}
