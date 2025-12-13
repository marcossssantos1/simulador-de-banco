package com.banco.ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.ms.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>{
	
}
