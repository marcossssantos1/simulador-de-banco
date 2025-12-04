package com.banco.ms.model;

import java.time.LocalDateTime;

abstract class Account {
	
	String name;
	LocalDateTime createAt;
	Double balance;
	
	public Account(String name, LocalDateTime createAt, Double balance) {
		this.name = name;
		this.createAt = createAt;
		this.balance = balance;
	}
	
	abstract void conta();

}
