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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.banco.ms.dto.ContaPfReponseDto;
import com.banco.ms.dto.ContaPfRequestDto;
import com.banco.ms.dto.ContaPfUpdateRequestDto;
import com.banco.ms.dto.PixKeyRequestDto;
import com.banco.ms.dto.PixKeyResponseDto;
import com.banco.ms.dto.TransactionFilterDto;
import com.banco.ms.dto.TransactionRequestDto;
import com.banco.ms.dto.TransactionResponseDto;
import com.banco.ms.dto.TransferRequestDto;
import com.banco.ms.enums.PixKeyType;
import com.banco.ms.enums.StatusAccount;
import com.banco.ms.enums.TransactiontType;
import com.banco.ms.enums.TransferType;
import com.banco.ms.exceptions.BadResquestException;
import com.banco.ms.exceptions.ConflictCpfException;
import com.banco.ms.exceptions.EntityNotFoundException;
import com.banco.ms.exceptions.ForbiddenOperationException;
import com.banco.ms.exceptions.InvalidAccountException;
import com.banco.ms.model.AccountPf;
import com.banco.ms.model.Pixkey;
import com.banco.ms.model.Transaction;
import com.banco.ms.repository.ContaPfRepository;
import com.banco.ms.repository.PixKeyRepository;
import com.banco.ms.repository.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class ContaPfService {

	@Autowired
	private ContaPfRepository repository;

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private PixKeyRepository pixKeyRepository;
	
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

	
	public String resolvePixKey(PixKeyRequestDto dto) {
		
		if(dto.pixType() == PixKeyType.ALEATORIA) {
			return UUID.randomUUID().toString();
		}
		
		if(dto.pixKey() == null || dto.pixKey().isBlank()) {
			throw new BadResquestException("A chave PIX é obrigatória esse tipo");
		}
		
		return dto.pixKey().trim();
		
		
		
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

	public void deleteAccount(Long id, StatusAccount status) {
		AccountPf acc = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não localizada com esse id " + id));
		
		validateStatusChange(acc, status);
		acc.setStatus(StatusAccount.ENCERRADA);
		repository.save(acc);
	}

	public ContaPfReponseDto toDto(AccountPf acc) {
		List<PixKeyResponseDto> keys = acc.getPixKey()
		        .stream()
		        .map(k -> new PixKeyResponseDto(k.getId(), k.getValue(), k.getType()))
		        .toList();

		    return new ContaPfReponseDto(
		            acc.getName(),
		            acc.getBalance(),
		            acc.getStatus(),
		            acc.getCpf(),
		            acc.getAgency(),
		            acc.getNumberAccount(),
		            acc.getIncome(),
		            keys
		    );
	}

	public AccountPf fromDto(ContaPfRequestDto dto) {
		AccountPf acc = new AccountPf();
		acc.setName(dto.name());
		acc.setBalance(BigDecimal.ZERO);
		acc.setStatus(StatusAccount.EM_ANALISE);
		if(repository.existsByCpf(dto.cpf())) {
			throw new ConflictCpfException("Esse CPF já está cadastrado em outra conta.");
		}
		acc.setCpf(dto.cpf());
		acc.setAgency("0001");
		acc.setIncome(dto.income());
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
		
		String finalKey = resolvePixKey(dto);
		
		validatePixKey(finalKey, dto.pixType());
		
		if(pixKeyRepository.existsByValue(finalKey)) {
			throw new BadResquestException("Essa chave PIX já está cadastrada.");
		}
		
		Pixkey pix = new Pixkey(finalKey, dto.pixType(), acc);
		
		acc.getPixKey().add(pix);
		repository.save(acc);
	}
	
	private void validatePixKey(String key, PixKeyType type) {

	    switch (type) {
	        case CPF -> {
	            if (!key.matches("\\d{11}"))
	                throw new BadResquestException("CPF inválido.");
	        }

	        case EMAIL -> {
	            if (!key.matches("^\\S+@\\S+\\.\\S+$"))
	                throw new BadResquestException("Email inválido.");
	        }

	        case TELEFONE -> {
	            if (!key.matches("^\\+55\\d{10,11}$"))
	                throw new BadResquestException("Telefone inválido. Use formato +5511999999999");
	        }

	        case ALEATORIA -> {
	            if (!key.matches("^[a-fA-F0-9\\-]{36}$"))
	                throw new BadResquestException("Chave aleatória inválida.");
	        }
	    }
	}
	
	@Transactional
	public void removePixKey(Long id, String value) {
		
		AccountPf acc = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não encontrada."));
		
		Pixkey pix = pixKeyRepository.findByValue(value).orElseThrow(() -> new EntityNotFoundException("Chave PIX não encontrada"));
				
		if(!pix.getAcc().getId().equals(id)) {
			throw new ForbiddenOperationException("Essa chave PIX não pertence a esta conta");
		}
		
		acc.getPixKey().remove(pix);
		
		pixKeyRepository.delete(pix);
		
		repository.save(acc);
				
	}
	
	private void validateStatusChange(AccountPf acc, StatusAccount status) {
		
		if(acc.getStatus() == StatusAccount.ATIVA 
				&& status == StatusAccount.ENCERRADA) {
			
			if(acc.getBalance().compareTo(BigDecimal.ZERO) > 0) {
				throw new BadResquestException("A conta não pode ser encerrada pois ainda possui saldo positivo.");
			}
			
			if(acc.getBalance().compareTo(BigDecimal.ZERO) < 0) {
				throw new ForbiddenOperationException("A conta não pode ser encerrada pois possui saldo negativo");
			}
		}
		
	}
	
	@Transactional
	public void validateAndActivate(Long id) {
		AccountPf acc = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Conta não encontrada."));
		
		if(acc.getStatus() != StatusAccount.EM_ANALISE) throw new BadResquestException("A conta não está em analise!");
		
		if(acc.getName() == null || acc.getName().isBlank()) throw new BadResquestException("Nome obrigatório para ativação da conta.");
		
		if(acc.getAgency() == null || acc.getAgency().isBlank()) throw new BadResquestException("Agencia não definida.");
		
		if(acc.getNumberAccount() == null || acc.getNumberAccount().isBlank()) throw new BadResquestException("Número da conta não foi gerado.");
		
		acc.setStatus(StatusAccount.ATIVA);
		repository.save(acc);
	}
	
	@Scheduled(fixedDelay = 30000)
	public void autoActivateAccounts() {
		
		List<AccountPf> acc = repository.findAllByStatus(StatusAccount.EM_ANALISE);
		
		acc.forEach(accs -> {
			try {
				validateAndActivate(accs.getId());
			} catch (Exception e) {
				throw new BadResquestException("Número da");
			}
		});
	}

}
