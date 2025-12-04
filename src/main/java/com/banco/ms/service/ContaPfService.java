package com.banco.ms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banco.ms.enums.StatusAccount;
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
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Conta não localizada com esse id " + id));
	}
	
	public AccountPf update(Long id,AccountPf acc) {
		AccountPf pf = repository.findById(id).orElseThrow(() -> new RuntimeException("Conta não localizada com esse id " + id));
		pf.setName(acc.getName());
		pf.setBalance(acc.getBalance());
		pf.setStatus(acc.getStatus());
		return repository.save(pf);
	}
	
	public void inative(Long id) {
		AccountPf acc = repository.findById(id).orElseThrow(() -> new RuntimeException("Conta não localizada com esse id " + id));
		acc.setStatus(StatusAccount.INATIVA);
		repository.save(acc);
	}

}
