package com.banco.ms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.banco.ms.enums.InvoiceStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Invoice {
	
	@Id 
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;
	
	private int month;
	private int year;
	
	private LocalDate date;
	
	private BigDecimal total = BigDecimal.ZERO;
	
	@ManyToOne
	private Card card;
	
	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	private List<Purchase> purchases = new ArrayList<>();
	
	@Enumerated(EnumType.STRING)
	private InvoiceStatus invoiceStatus;
	
	@Column(precision = 19, scale = 2)
	private BigDecimal paidAmount = BigDecimal.ZERO;
	
	public void addPurchase(Purchase purchase) {
		purchases.add(purchase);
		total = total.add(purchase.getAmount());
	}
	
	public BigDecimal getRemaining() {
		return total.subtract(paidAmount);
	}
	
	public Invoice() {
	}

	public Invoice(Long id, int month, int year, LocalDate date, BigDecimal total, Card card, List<Purchase> purchases,
			InvoiceStatus invoiceStatus, BigDecimal paidAmount) {
		super();
		this.id = id;
		this.month = month;
		this.year = year;
		this.date = date;
		this.total = total;
		this.card = card;
		this.purchases = purchases;
		this.invoiceStatus = invoiceStatus;
		this.paidAmount = paidAmount;
	}

	public Invoice(Long id, int month, int year, LocalDate date, BigDecimal total, Card card,
			List<Purchase> purchases) {
		super();
		this.id = id;
		this.month = month;
		this.year = year;
		this.date = date;
		this.total = total;
		this.card = card;
		this.purchases = purchases;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public List<Purchase> getPurchases() {
		return purchases;
	}

	public void setPurchases(List<Purchase> purchases) {
		this.purchases = purchases;
	}

	public InvoiceStatus getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
		this.invoiceStatus = invoiceStatus.ABERTA;
	}
	
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
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
		Invoice other = (Invoice) obj;
		return Objects.equals(id, other.id);
	}

}
