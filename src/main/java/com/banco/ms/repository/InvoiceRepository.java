package com.banco.ms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.ms.model.Card;
import com.banco.ms.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>{

	Optional<Invoice> findByCardAndMonthAndYear(Card card, int monthValue, int year);

	


	
}
