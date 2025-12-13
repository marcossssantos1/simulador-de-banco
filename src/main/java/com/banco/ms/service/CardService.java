package com.banco.ms.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.banco.ms.dto.CarResponseDto;
import com.banco.ms.dto.CardPurchaseDto;
import com.banco.ms.dto.CardRequestDto;
import com.banco.ms.dto.PurchaseRequestDto;
import com.banco.ms.enums.CardStatus;
import com.banco.ms.enums.CardTier;
import com.banco.ms.enums.StatusAccount;
import com.banco.ms.exceptions.BadResquestException;
import com.banco.ms.exceptions.EntityNotFoundException;
import com.banco.ms.model.AccountPf;
import com.banco.ms.model.Card;
import com.banco.ms.model.Invoice;
import com.banco.ms.model.Purchase;
import com.banco.ms.repository.CardRepository;
import com.banco.ms.repository.ContaPfRepository;
import com.banco.ms.repository.InvoiceRepository;
import com.banco.ms.repository.PurchaseRepository;

import jakarta.transaction.Transactional;

@Service
public class CardService {
	
	@Autowired
	private CardRepository cardRepository;
	
	@Autowired
	private ContaPfRepository contaRepository;
	
	@Autowired
	private InvoiceRepository invoiceRepository;
	
	@Autowired
	private PurchaseRepository purchaseRepository;
	
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
		
		
		if(acc.getIncome() == null || acc.getIncome().compareTo(BigDecimal.ZERO) <= 0) {
			throw new BadResquestException("Renda obrigatória para solicitar um cartão.");
		}
		
		CardTier tier = selectTier(acc.getIncome());
		
		if(cardRepository.existsByOwnerIdAndCardTier(acc.getId(), tier)) throw new BadResquestException("Você já possuium cartão desse tipo.");
		
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
	
	public List<CarResponseDto> listCards(String cpf){
		AccountPf acc = contaRepository.findByCpf(cpf).orElseThrow(() -> new EntityNotFoundException("Conta não encontrada."));
		
		return cardRepository.findAllByOwnerId(acc.getId()).stream().map(CarResponseDto::new).toList();
	}
	
	@Transactional
	public void blockCard(Long id) {
		Card card = cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não encontrada."));
		card.setCardStatus(CardStatus.BLOQUEADO);
	}
	
	@Transactional
	public void cancelCard(Long id) {
		Card card = cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não encontrada."));
		card.setCardStatus(CardStatus.CANCELADO);
	}
	
	@Transactional
	public void makePurchase(CardPurchaseDto dto) {
		Card card = cardRepository.findByCardNumber(dto.cardNumber()).orElseThrow(() -> new EntityNotFoundException("Conta não encontrada."));
		
		if(card.getCardStatus() != CardStatus.ATIVO) throw new BadResquestException("Cartão não está ativo.");
		
		BigDecimal newUsed = card.getUsedlimit().add(dto.amount());
		
		if(newUsed.compareTo(card.getLimitValue()) > 0) throw new BadResquestException("Limite insuficiente");
		
		card.setUsedlimit(newUsed);
		cardRepository.save(card);
	}
	
	@Scheduled(cron = "0 0 0 1 * *")
	@Transactional
	public void closeInvoice() {
		List<Card> cards = cardRepository.findAll();
		
		cards.forEach(card -> {
			if(card.getUsedlimit().compareTo(BigDecimal.ZERO) > 0) {
				if(card.getUsedlimit().compareTo(card.getLimitValue()) > 0) {
					card.setCardStatus(CardStatus.BLOQUEADO);
				}
				
				card.setUsedlimit(BigDecimal.ZERO);
			}
		});
	}

	@Transactional
	public Purchase makePurchase(Long id, CardPurchaseDto dto) {
		
			Card card = cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cartão não encontrado."));
			
			if(card.getCardStatus() != CardStatus.ATIVO) throw new BadResquestException("Cartão bloqueado/cancelado.");
			
			if(card.getLimitValue().compareTo(dto.amount()) < 0) throw new BadResquestException("Limite insuficiente");
			
			LocalDate date = LocalDate.now();
			
			Invoice invoice = invoiceRepository.findByCardAndMonthAndYear(card, date.getMonthValue(), date.getYear())
					.orElseGet(() -> {
						Invoice newInvoice = new Invoice();
						
						newInvoice.setMonth(date.getMonthValue());
						newInvoice.setYear(date.getYear());
						newInvoice.setDate(LocalDate.of(date.getYear(), date.getMonthValue(), 10));
						
						return invoiceRepository.save(newInvoice);
					});
			
			Purchase p = new Purchase();
			p.setAmount(dto.amount());
			p.setDescription(dto.description());
			p.setDate(LocalDateTime.now());
			p.setCard(card);
			p.setInvoice(invoice);
			
			card.setUsedlimit(dto.amount());
			
			invoice.addPurchase(p);
			
			purchaseRepository.save(p);
			invoiceRepository.save(invoice);
			cardRepository.save(card);
			
			return p;
	}
	
}
