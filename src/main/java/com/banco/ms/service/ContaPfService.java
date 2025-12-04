package com.banco.ms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banco.ms.dto.ContaPfReponseDto;
import com.banco.ms.dto.ContaPfRequestDto;
import com.banco.ms.enums.StatusAccount;
import com.banco.ms.exceptions.EntityNotFoundException;
import com.banco.ms.model.AccountPf;
import com.banco.ms.repository.ContaPfRepository;

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
	
	public AccountPf update(Long id, ContaPfRequestDto acc) {
		AccountPf pf = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não localizada com esse id " + id));
		pf.setName(acc.name());
		pf.setBalance(acc.balance());
		if(acc.status() != null) {
			pf.setStatus(acc.status());
		}
		return repository.save(pf);
	}
	
	public void inative(Long id) {
		AccountPf acc = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não localizada com esse id " + id));
		acc.setStatus(StatusAccount.INATIVA);
		repository.save(acc);
	}
	
	public ContaPfReponseDto toDto(AccountPf acc) {
		return new ContaPfReponseDto(acc.getName(), acc.getBalance(),acc.getStatus());
	}
	
	public AccountPf fromDto(ContaPfRequestDto dto) {
		AccountPf acc = new AccountPf();
		acc.setName(dto.name());
		acc.setBalance(dto.balance());
		acc.setStatus(StatusAccount.EM_ANALISE);
		return acc;
	}

}
