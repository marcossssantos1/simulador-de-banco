package com.banco.ms.exceptions;

public class BadResquestException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public BadResquestException(String msg) {
		super(msg);
	}

}
