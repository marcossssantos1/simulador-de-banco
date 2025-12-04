package com.banco.ms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import com.banco.ms.enums.StatusAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "conta_pf")
public class AccountPf{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@CreationTimestamp
	private LocalDateTime createAt;
	@Column(precision = 19, scale = 2)
	private BigDecimal balance;
	@Enumerated(EnumType.STRING)
	private StatusAccount status;
	
	public AccountPf() {
	}
	
	

	public AccountPf(String name, BigDecimal balance, StatusAccount status) {
		super();
		this.name = name;
		this.balance = balance;
		this.status = status;
	}



	public AccountPf(Long id, String name, LocalDateTime createAt, BigDecimal balance, StatusAccount status) {
		super();
		this.id = id;
		this.name = name;
		this.createAt = createAt;
		this.balance = balance;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balances) {
		balance = balances;
	}

	public StatusAccount getStatus() {
		return status;
	}

	public void setStatus(StatusAccount status) {
		this.status = status;
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
		AccountPf other = (AccountPf) obj;
		return Objects.equals(id, other.id);
	}	

}
