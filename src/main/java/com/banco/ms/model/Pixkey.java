package com.banco.ms.model;

import java.util.Objects;

import com.banco.ms.enums.PixKeyType;

import jakarta.persistence.Column;
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
@Table(name = "pix_keys")
public class Pixkey {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String value;
	
	@Enumerated(EnumType.STRING)
	private PixKeyType type;
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private AccountPf account;
	
	public Pixkey() {
	}

	
	public Pixkey(String value, PixKeyType type, AccountPf account) {
		super();
		this.value = value;
		this.type = type;
		this.account = account;
	}



	public Pixkey(Long id, String value, PixKeyType type, AccountPf account) {
		super();
		this.id = id;
		this.value = value;
		this.type = type;
		this.account = account;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public PixKeyType getType() {
		return type;
	}

	public void setType(PixKeyType type) {
		this.type = type;
	}

	public AccountPf getAcc() {
		return account;
	}

	public void setAcc(AccountPf acc) {
		this.account = acc;
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
		Pixkey other = (Pixkey) obj;
		return Objects.equals(id, other.id);
	}
	
	

}
