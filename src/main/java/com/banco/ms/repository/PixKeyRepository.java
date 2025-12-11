package com.banco.ms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.ms.model.Pixkey;

public interface PixKeyRepository extends JpaRepository<Pixkey, Long> {

	boolean existsByValue(String value);

	Optional<Pixkey> findByValue(String value);
	
}
