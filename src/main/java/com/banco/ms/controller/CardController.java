package com.banco.ms.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banco.ms.dto.CarResponseDto;
import com.banco.ms.dto.CardPurchaseDto;
import com.banco.ms.dto.CardRequestDto;
import com.banco.ms.dto.InvoiceResponse;
import com.banco.ms.exceptions.EntityNotFoundException;
import com.banco.ms.model.Card;
import com.banco.ms.model.Invoice;
import com.banco.ms.repository.InvoiceRepository;
import com.banco.ms.service.CardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cartões")
@RequiredArgsConstructor
public class CardController {
	
	@Autowired
	private CardService service;
	
	@Autowired
	private InvoiceRepository invoiceRepository;
	
	
	@PostMapping("/solicitar")
	public ResponseEntity<CarResponseDto> request(@RequestBody @Valid CardRequestDto dto){
		return ResponseEntity.ok(new CarResponseDto(service.requestCard(dto)));
	}
	
	@GetMapping("/listar")
	public ResponseEntity<List<CarResponseDto>> request(@RequestParam String cpf){
		return ResponseEntity.ok(service.listCards(cpf));
	}
	
	@PutMapping("/{id}/cancelar")
	public ResponseEntity<CarResponseDto> request(@PathVariable Long id){
		service.cancelCard(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/compra")
	public ResponseEntity<Void> request(@RequestBody @Valid CardPurchaseDto dto){
		service.makePurchase(dto);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/{id}/fatura")
	public InvoiceResponse getCurrentInvoice(@PathVariable Long id) {
		Card card = service.findById(id);
		LocalDate today = LocalDate.now();
		Invoice invoice = invoiceRepository.findByCardAndMonthAndYear(card, today.getMonthValue(), today.getYear())
				.orElseThrow(() -> new EntityNotFoundException("Nenhuma fatura encontrada."));
		
		return InvoiceResponse.from(invoice);
	}
	
}
