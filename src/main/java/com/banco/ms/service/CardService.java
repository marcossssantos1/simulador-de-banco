package com.banco.ms.service;

import java.io.ObjectInputFilter.Status;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banco.ms.dto.CardRequestDto;
import com.banco.ms.enums.CardStatus;
import com.banco.ms.enums.CardTier;
import com.banco.ms.enums.StatusAccount;
import com.banco.ms.exceptions.BadResquestException;
import com.banco.ms.exceptions.EntityNotFoundException;
import com.banco.ms.model.AccountPf;
import com.banco.ms.model.Card;
import com.banco.ms.repository.CardRepository;
import com.banco.ms.repository.ContaPfRepository;

import jakarta.transaction.Transactional;

@Service
public class CardService {
	
	@Autowired
	private CardRepository cardRepository;
	
	@Autowired
	private ContaPfRepository contaRepository;
	
	@Transactional
	public Card requestCard(CardRequestDto dto){
		
		AccountPf acc = contaRepository.findByCpf(dto.cpf()).orElseThrow(
				() -> new EntityNotFoundException("Conta não encontrada"));
		
		if(acc.getStatus() != StatusAccount.ATIVA) {
			throw new BadResquestException("Conta precisa estar ativa para solicitar um cartão");
		};
		
		if(acc.getBalance().compareTo(new BigDecimal("-500")) < 0 ) {
			throw new BadResquestException("Conta está abaixo de -500. Cartão bloqueado automaticamente");
		}
		
		if(acc.getIncome() == null || acc.getIncome().compareTo(BigDecimal.ZERO)) {
			throw new BadResquestException("Renda obrigatória para solicitar um cartão.");
		}
		
		CardTier tier = selectTier(acc.getIncome());
		
		if(cardRepository.existsByOwnerIdAndTier(acc.getId(), tier)) throw new BadResquestException("Você já possuium cartão desse tipo.");
		
		Card card = new Card();
		card.setCardTier(tier);
		card.setLimitValue(calculateLimit(tier));
		card.setCardNumber(generateCardNumber());
		card.setCvv(generateCVV());
		card.setExpiration(LocalDate.now().plusYears(5));
		card.setCardStatus(CardStatus.ATIVO);
		card.setOwner(acc);
		
		return cardRepository.save(card);
	}
	
	private CardTier selectTier(BigDecimal income) {
		
		if(income.compareTo(new BigDecimal("2000")) <= 0) return CardTier.BASIC;
		
		if(income.compareTo(new BigDecimal("5000")) <= 0) return CardTier.SILVER;
		
		if(income.compareTo(new BigDecimal("10000")) <= 0) return CardTier.GOLD;
		
		return CardTier.BLACK;
		
	}
	
	private BigDecimal calculateLimit(CardTier tier) {
		return switch (tier) {

		case BASIC -> new BigDecimal("4000");
		case SILVER -> new BigDecimal("4000");
		case GOLD -> new BigDecimal("4000");
		case BLACK -> new BigDecimal("4000");
		
		};
	}
	
	private String generateCardNumber() {
		return String.valueOf((long)(Math.random() * 900 + 100));
	}
	
	private String generateCVV() {
		return String.valueOf((int)(Math.random() * 900 + 100));
	}

}
