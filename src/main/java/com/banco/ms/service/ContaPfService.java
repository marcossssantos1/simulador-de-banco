package com.banco.ms.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.banco.ms.dto.ContaPfReponseDto;
import com.banco.ms.dto.ContaPfRequestDto;
import com.banco.ms.dto.ContaPfUpdateRequestDto;
import com.banco.ms.dto.PixKeyRequestDto;
import com.banco.ms.dto.TransactionFilterDto;
import com.banco.ms.dto.TransactionRequestDto;
import com.banco.ms.dto.TransactionResponseDto;
import com.banco.ms.dto.TransferRequestDto;
import com.banco.ms.enums.PixKeyType;
import com.banco.ms.enums.StatusAccount;
import com.banco.ms.enums.TransactiontType;
import com.banco.ms.enums.TransferType;
import com.banco.ms.exceptions.BadResquestException;
import com.banco.ms.exceptions.EntityNotFoundException;
import com.banco.ms.exceptions.InvalidAccountException;
import com.banco.ms.model.AccountPf;
import com.banco.ms.model.Transaction;
import com.banco.ms.repository.ContaPfRepository;
import com.banco.ms.repository.TransactionRepository;
import com.banco.ms.util.PixKeyValidator;

import jakarta.transaction.Transactional;

@Service
public class ContaPfService {

	@Autowired
	private ContaPfRepository repository;

	@Autowired
	private TransactionRepository transactionRepository;
	
	public String generateAccountNumber() {
		int numero = (int)(Math.random() * 90000000) + 10000000;
		return String.valueOf(numero);
	}
	
	public void validateActiveAccount(AccountPf acc) {
		if(acc.getStatus() != StatusAccount.ATIVA) {
			throw new InvalidAccountException(
					"A operação não pode ser realizada. A conta (" +
				acc.getNumberAccount()	+ ") está com status." + acc.getStatus() );	
			}
	}
	
	public AccountPf findByPixKey(String pixKey) {
		return repository.findByPixKey(pixKey).orElseThrow(() -> new EntityNotFoundException("Chave PIX não encontrada."));
				
	}
	
	public void validateTedTim() {
		LocalDateTime now = LocalDateTime.now();
		DayOfWeek day = now.getDayOfWeek();
		int hour = now.getHour();
		
		if(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
			throw new BadResquestException("TED só pode ser realizada em dias úteis.");
		}
		
		if(hour < 8 || hour > 17) {
			throw new BadResquestException("TED só permitida entre 08:00 e 17:00.");
		}
	}

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
		return new ContaPfReponseDto(acc.getName(), acc.getBalance(), acc.getStatus(),
				acc.getCpf(), acc.getAgency(), acc.getNumberAccount());
	}

	public AccountPf fromDto(ContaPfRequestDto dto) {
		AccountPf acc = new AccountPf();
		acc.setName(dto.name());
		acc.setBalance(BigDecimal.ZERO);
		acc.setStatus(StatusAccount.EM_ANALISE);
		acc.setCpf(dto.cpf());
		acc.setAgency("0001");
		acc.setNumberAccount(generateAccountNumber());
		return acc;
	}

	@Transactional
	public ContaPfReponseDto deposit(Long id, TransactionRequestDto dto) {
		AccountPf acc = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não localizada com esse id " + id));

		validateActiveAccount(acc);

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
		
		validateActiveAccount(acc);

		if (acc.getBalance().compareTo(dto.amount()) < 0) {
			throw new BadResquestException("Saldo insuficiente para saque.");
		}

		acc.setBalance(acc.getBalance().subtract(dto.amount()));
		AccountPf saved = repository.save(acc);

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
		
		AccountPf accFrom = repository.findById(dto.fromAccountId()).orElseThrow(
				() -> new EntityNotFoundException("Conta de origem não encontrada."));
		validateActiveAccount(accFrom);
		
		AccountPf accTo;
		
		if(dto.type() == TransferType.PIX) {
			accTo = findByPixKey(dto.pixKey());
		} else {
			validateTedTim();
			accTo = repository.findById(dto.toAccountId())
					.orElseThrow(() -> new EntityNotFoundException("Conta de destino não encontrada."));
		}
		
		validateActiveAccount(accTo);
		
		if(dto.fromAccountId().equals(dto.toAccountId())) {
			throw new BadResquestException("A conta de origem não pode ser igual a conta de destino.");
		}
		
		if(accFrom.getBalance().compareTo(dto.amount()) < 0) {
			throw new BadResquestException("Saldo insuficiente para transfrência");
		}
		
		accFrom.setBalance(accFrom.getBalance().subtract(dto.amount()));
		repository.save(accFrom);
		
		accTo.setBalance(accTo.getBalance().add(dto.amount()));
		repository.save(accTo);
		
		Transaction sent = new Transaction(null, dto.amount(), TransactiontType.TRANSFERENCIA_ENVIADA,
				LocalDateTime.now(), accFrom);
		transactionRepository.save(sent);
		
		Transaction received = new Transaction(null, dto.amount(), TransactiontType.TRANSFERENCIA_RECEBIDA,
				LocalDateTime.now(), accTo);
		transactionRepository.save(received);
		
	}
	
	@Transactional
	public void registerPixKey(Long id, PixKeyRequestDto dto) {
		
		AccountPf acc = repository.findById(id).orElseThrow(
				() -> new EntityNotFoundException("Conta não encontrada."));
		
		if(acc.getStatus() != StatusAccount.ATIVA) {
			throw new BadResquestException("Conta não encontrada.");
		}
		
		if(acc.getPixKey() != null) {
			throw new BadResquestException("A conta já possui uma chave PIX cadastrada.");
		}
		
		if(PixKeyValidator.isValidPixKey(dto.pixKey())) {
			throw new BadResquestException("Conta inválida.");
		}
		
		String finalKey;
		
		if(dto.pixType() == PixKeyType.ALEATORIA) {
			finalKey = UUID.randomUUID().toString();
		}
		
		if (PixKeyValidator.isValidPixKey(dto.pixKey())) {
	        throw new BadResquestException("Chave PIX inválida");
	    }
		
		if(dto.pixKey().matches("^[a-fA-f0-9\\-]{36}$")) {
			throw new BadResquestException("CPF inválido.");
		} else {
			finalKey = dto.pixKey();
		}
		
		if(dto.pixKey().matches("^\\+55\\d{10,11}$")) {
			throw new BadResquestException("Telefone inválido.");
		} else {
			finalKey = dto.pixKey();
		}
		
		if(dto.pixKey().matches("^\\S+@\\S+\\.\\S+$")) {
			throw new BadResquestException("Email inválido.");
		} else {
			finalKey = dto.pixKey();
		}
		
		acc.setPixKey(finalKey);
		repository.save(acc);
	}

}
