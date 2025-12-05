package com.banco.ms.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banco.ms.dto.ContaPfReponseDto;
import com.banco.ms.dto.ContaPfRequestDto;
import com.banco.ms.dto.ContaPfUpdateRequestDto;
import com.banco.ms.dto.TransactionRequestDto;
import com.banco.ms.enums.StatusAccount;
import com.banco.ms.exceptions.BadResquestException;
import com.banco.ms.exceptions.EntityNotFoundException;
import com.banco.ms.model.AccountPf;
import com.banco.ms.repository.ContaPfRepository;

import jakarta.transaction.Transactional;

@Service
public class ContaPfService {
	
	@Autowired
	private ContaPfRepository repository;
	
	public AccountPf create(AccountPf pf) {
		return repository.save(pf);
	}
	
	public List<AccountPf> findAll() {
		return repository.findAll();
	}
	
	public AccountPf findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não localizada com esse id " + id));
	}
	
	public AccountPf update(Long id, ContaPfUpdateRequestDto acc) {
		AccountPf pf = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não localizada com esse id " + id));
		
		if(pf.getStatus() == StatusAccount.INATIVA) {
			throw new BadResquestException("Não é permitido atualizar conta inativa");
		}
		
		if(acc.status() != null) {
			pf.setStatus(acc.status());
		}
		
		pf.setName(acc.name());
		
		return repository.save(pf);
	}
	
	public void inative(Long id) {
		AccountPf acc = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não localizada com esse id " + id));
		acc.setStatus(StatusAccount.INATIVA);
		repository.save(acc);
	}
	
	public void deleteAccount(Long id) {
		AccountPf acc = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não localizada com esse id " + id));
		acc.setStatus(StatusAccount.ENCERRADA);
		repository.save(acc);
	}
	
	public ContaPfReponseDto toDto(AccountPf acc) {
		return new ContaPfReponseDto(acc.getName(), acc.getBalance(),acc.getStatus());
	}
	
	public AccountPf fromDto(ContaPfRequestDto dto) {
		AccountPf acc = new AccountPf();
		acc.setName(dto.name());
		acc.setBalance(BigDecimal.ZERO);
		acc.setStatus(StatusAccount.EM_ANALISE);
		return acc;
	}
	
	@Transactional
	public ContaPfReponseDto deposit(Long id, TransactionRequestDto dto) {
		AccountPf acc = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não localizada com esse id " + id));
		
		if(acc.getStatus() == StatusAccount.INATIVA) {
			throw new BadResquestException("Não é permitido depositar em uma conta inativa");
		}
		
		acc.setBalance(acc.getBalance().add(dto.amount()));
		AccountPf saved =  repository.save(acc);
		return toDto(saved);
	}
	
	@Transactional
	public ContaPfReponseDto withdraw(Long id, TransactionRequestDto dto) {
	    AccountPf acc = repository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

	    if (acc.getStatus() == StatusAccount.INATIVA) {
	        throw new BadResquestException("Não é possível sacar de uma conta inativa.");
	    }

	    if (acc.getBalance().compareTo(dto.amount()) < 0) {
	        throw new BadResquestException("Saldo insuficiente para saque.");
	    }

	    acc.setBalance(acc.getBalance().subtract(dto.amount()));
	    AccountPf saved = repository.save(acc);
	    return toDto(saved);
	}


}
