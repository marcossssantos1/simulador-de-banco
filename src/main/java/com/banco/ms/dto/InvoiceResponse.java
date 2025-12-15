package com.banco.ms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.banco.ms.model.Invoice;

public record InvoiceResponse (
		int month, 
		int year, 
		BigDecimal total, 
		LocalDate date, 
		List<CardPurchaseDto> purchases
		){
	
	public static InvoiceResponse from(Invoice invoice) {
		return new InvoiceResponse(
				invoice.getMonth(),
				invoice.getYear(),
				invoice.getTotal(),
				invoice.getDate(),
				invoice.getPurchases().stream()
				.map(CardPurchaseDto::from)
				.toList()
				);
	}
}
