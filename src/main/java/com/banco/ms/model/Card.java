package com.banco.ms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.banco.ms.enums.CardStatus;
import com.banco.ms.enums.CardTier;

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
@Table(name = "cards")
public class Card {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 16)
	private String cardNumber;
	
	@Column(nullable = false, length = 3)
	private String cvv;
	
	@Column(nullable = false)
	private LocalDate expiration;
	
	@Enumerated(EnumType.STRING)
	private CardStatus cardStatus;

	@Enumerated(EnumType.STRING)
	private CardTier cardTier;
	
	@Column(precision = 19, scale = 2)
	private BigDecimal limitValue;
	
	@Column(precision = 19, scale = 2)
	private BigDecimal usedlimit = BigDecimal.ZERO;
	
	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private AccountPf owner;
	
	public Card() {
	}

	public Card(Long id, String cardNumber, String cvv, LocalDate expiration, CardStatus cardStatus, CardTier cardTier,
			BigDecimal limitValue, BigDecimal usedlimit, AccountPf owner) {
		super();
		this.id = id;
		this.cardNumber = cardNumber;
		this.cvv = cvv;
		this.expiration = expiration;
		this.cardStatus = cardStatus;
		this.cardTier = cardTier;
		this.limitValue = limitValue;
		this.usedlimit = usedlimit;
		this.owner = owner;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public LocalDate getExpiration() {
		return expiration;
	}

	public void setExpiration(LocalDate expiration) {
		this.expiration = expiration;
	}

	public CardStatus getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(CardStatus cardStatus) {
		this.cardStatus = cardStatus;
	}

	public CardTier getCardTier() {
		return cardTier;
	}

	public void setCardTier(CardTier cardTier) {
		this.cardTier = cardTier;
	}

	public BigDecimal getLimitValue() {
		return limitValue;
	}

	public void setLimitValue(BigDecimal limitValue) {
		this.limitValue = limitValue;
	}

	public BigDecimal getUsedlimit() {
		return usedlimit;
	}

	public void setUsedlimit(BigDecimal usedlimit) {
		this.usedlimit = usedlimit;
	}

	public AccountPf getOwner() {
		return owner;
	}

	public void setOwner(AccountPf owner) {
		this.owner = owner;
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
		Card other = (Card) obj;
		return Objects.equals(id, other.id);
	}
	
	
}
