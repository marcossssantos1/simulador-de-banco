package com.banco.ms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.ms.enums.CardTier;
import com.banco.ms.model.Card;

public interface CardRepository extends JpaRepository<Card, Long>{

	List<Card> findAllByOwnerId(Long ownerId);

	boolean existsByOwnerIdAndTier(Long ownerId, CardTier tier);


	
}
