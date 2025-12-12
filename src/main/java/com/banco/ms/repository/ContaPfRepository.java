package com.banco.ms.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.ms.enums.StatusAccount;
import com.banco.ms.model.AccountPf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public interface ContaPfRepository extends JpaRepository<AccountPf, Long>{

	Optional<AccountPf> findByPixKey(String pixKey);

	boolean existsByPixKey(String finalKey);

	List<AccountPf> findAllByStatus(StatusAccount emAnalise);

	boolean existsByCpf(
			@NotBlank(message = "CPF é obrigatório para abrir uma conta") @CPF @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 números") String cpf);

	Optional <AccountPf> findByCpf(@NotBlank String cpf);

	
}
