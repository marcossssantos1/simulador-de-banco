package com.banco.ms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import com.banco.ms.enums.TransactiontType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transctions")
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private BigDecimal amount;
	
	@Enumerated(EnumType.STRING)
	private TransactiontType type;
	
	@CreationTimestamp
	private LocalDateTime date;
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private AccountPf account;
	
	public Transaction() {
	}

	public Transaction(Long id, BigDecimal amount, TransactiontType type, LocalDateTime date, AccountPf account) {
		super();
		this.id = id;
		this.amount = amount;
		this.type = type;
		this.date = date;
		this.account = account;
	}
	
	

	public Transaction(BigDecimal amount, TransactiontType type, AccountPf account) {
		super();
		this.amount = amount;
		this.type = type;
		this.account = account;
	}

	public Transaction(BigDecimal amount, TransactiontType type, LocalDateTime date, AccountPf account) {
		super();
		this.amount = amount;
		this.type = type;
		this.date = date;
		this.account = account;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public TransactiontType getType() {
		return type;
	}

	public void setType(TransactiontType type) {
		this.type = type;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public AccountPf getAccount() {
		return account;
	}

	public void setAccount(AccountPf account) {
		this.account = account;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		return Objects.equals(id, other.id);
	}
	
	
	

}
