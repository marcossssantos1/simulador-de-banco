package com.banco.ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.ms.enums.CardTier;
import com.banco.ms.model.Card;

import jakarta.validation.constraints.NotNull;

public interface CardRepository extends JpaRepository<Card, Long>{

	List<Card> findAllByOwnerId(Long ownerId);

	boolean existsByOwnerIdAndCardTier(Long ownerId, CardTier tier);

	Optional<Card> findByCardNumber(@NotNull String cardNumber);


	
}
