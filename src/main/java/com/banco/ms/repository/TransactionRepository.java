package com.banco.ms.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.ms.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
	
	Page<Transaction> findByAccountId(Long id, Pageable pageable);
	Page<Transaction> findByAccountIdAndDateBetween(
			Long accountId,
			LocalDateTime startDate,
			LocalDateTime endDate,
			Pageable pageable
	);
}
