package com.banco.ms.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.banco.ms.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.banco.ms.enums.StatusAccount;
import com.banco.ms.enums.TransactiontType;
import com.banco.ms.exceptions.BadResquestException;
import com.banco.ms.exceptions.EntityNotFoundException;
import com.banco.ms.model.AccountPf;
import com.banco.ms.model.Transaction;
import com.banco.ms.repository.ContaPfRepository;
import com.banco.ms.repository.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class ContaPfService {

	@Autowired
	private ContaPfRepository repository;

	@Autowired
	private TransactionRepository transactionRepository;

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

		if (pf.getStatus() == StatusAccount.INATIVA) {
			throw new BadResquestException("Não é permitido atualizar conta inativa");
		}

		if (acc.status() != null) {
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
		return new ContaPfReponseDto(acc.getName(), acc.getBalance(), acc.getStatus());
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

		if (acc.getStatus() == StatusAccount.INATIVA) {
			throw new BadResquestException("Não é permitido depositar em uma conta inativa");
		}

		acc.setBalance(acc.getBalance().add(dto.amount()));
		AccountPf saved = repository.save(acc);

		Transaction transaction = new Transaction(dto.amount(), TransactiontType.DEPOSITO, acc);

		transactionRepository.save(transaction);

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

		// Salvar a transação
		Transaction transaction = new Transaction(dto.amount(), TransactiontType.SAQUE, acc);

		transactionRepository.save(transaction);

		return toDto(saved);
	}

	public Page<TransactionResponseDto> getHistory(Long accountId, int page, int size) {

		repository.findById(accountId)
				.orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

		Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

		Page<Transaction> pageResult =
				transactionRepository.findByAccountId(accountId, pageable);

		return pageResult.map(t ->
				new TransactionResponseDto(
						t.getId(),
						t.getAmount(),
						t.getType(),
						t.getDate()
				)
		);
	}

	public Page<TransactionResponseDto> getHistoryFiltered(Long id,
														   TransactionFilterDto filter, Pageable pageable) {

		repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

		LocalDateTime start = filter.startDate();
		LocalDateTime end = filter.endDate();

		if (start != null && end != null && end.isBefore(start)) {
			throw new BadResquestException("A data final não pode ser anterior à data inicial.");
		}

		Page<Transaction> page = transactionRepository.findByAccountIdAndDateBetween(
				id,
				filter.startDate(),
				filter.endDate(),
				pageable
		);

		return page.map(t -> new TransactionResponseDto(
				t.getId(),
				t.getAmount(),
				t.getType(),
				t.getDate()
		));
	}
	
	
	@Transactional
	public void transfer(TransferRequestDto dto) {
		
		if(dto.fromAccountId().equals(dto.toAccountId())) {
			throw new BadResquestException("A conta de origem não pode ser igual a conta de destino.");
		}
		
		AccountPf accFrom = repository.findById(dto.fromAccountId()).orElseThrow(
				() -> new EntityNotFoundException("Conta de origem não encontrada."));
		
		AccountPf accTo = repository.findById(dto.fromAccountId()).orElseThrow(
				() -> new EntityNotFoundException("Conta de origem não encontrada."));
		
		if(accFrom.getStatus() == StatusAccount.INATIVA) {
			throw new BadResquestException("A conta de origem está inativa");
		}
		
		if(accTo.getStatus() == StatusAccount.INATIVA) {
			throw new BadResquestException("A conta de destino está inativa");
		}
		
		if(accFrom.getBalance().compareTo(dto.amount()) < 0) {
			throw new BadResquestException("Saldo insuficiente para transfrência");
		}
		
		accFrom.setBalance(accFrom.getBalance().subtract(dto.amount()));
		
		accTo.setBalance(accTo.getBalance().add(dto.amount()));
		
		repository.save(accFrom);
		repository.save(accTo);
		
		transactionRepository.save(new Transaction(null, dto.amount(), TransactiontType.TRANSFERENCIA_ENVIADA,
				LocalDateTime.now(), accFrom));
		
		transactionRepository.save(new Transaction(null, dto.amount(), TransactiontType.TRANSFERENCIA_RECEBIDA,
				LocalDateTime.now(), accTo));
		
	}

}
