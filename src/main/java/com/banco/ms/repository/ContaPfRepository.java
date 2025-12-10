package com.banco.ms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.ms.model.AccountPf;

public interface ContaPfRepository extends JpaRepository<AccountPf, Long>{

	Optional<AccountPf> findByPixKey(String pixKey);

	boolean existsByPixKey(String finalKey);

	
}
