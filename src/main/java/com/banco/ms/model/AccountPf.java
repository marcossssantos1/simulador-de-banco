package com.banco.ms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import com.banco.ms.enums.StatusAccount;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	
	@Column(nullable = false, length = 11, unique = true)
	private String cpf;
	
	@Column(nullable = false)
	private String agency;
	
	@Column(nullable = false, unique = true)
	private String numberAccount;
	
	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Pixkey> pixKey = new ArrayList<>();
	
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
	
	

	public AccountPf(Long id, String name, LocalDateTime createAt, BigDecimal balance, String cpf, String agency,
			String numberAccount, List<Pixkey> pixKey, StatusAccount status) {
		super();
		this.id = id;
		this.name = name;
		this.createAt = createAt;
		this.balance = balance;
		this.cpf = cpf;
		this.agency = agency;
		this.numberAccount = numberAccount;
		this.pixKey = pixKey;
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

	public AccountPf(Long id, String name, LocalDateTime createAt, BigDecimal balance, String cpf, String agency,
			String numberAccount, StatusAccount status) {
		super();
		this.id = id;
		this.name = name;
		this.createAt = createAt;
		this.balance = balance;
		this.cpf = cpf;
		this.agency = agency;
		this.numberAccount = numberAccount;
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
	
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getNumberAccount() {
		return numberAccount;
	}

	public void setNumberAccount(String numberAccount) {
		this.numberAccount = numberAccount;
	}

	public List<Pixkey> getPixKey() {
		return pixKey;
	}

	public void setPixKey(List<Pixkey> pixKey) {
		this.pixKey = pixKey;
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
