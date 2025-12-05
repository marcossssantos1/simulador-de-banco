package com.banco.ms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.ms.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
	
	List<Transaction> findByAccountId(Long accountId);
}
