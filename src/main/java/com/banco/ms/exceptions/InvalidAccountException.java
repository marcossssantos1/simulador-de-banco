package com.banco.ms.exceptions;

public class InvalidAccountException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InvalidAccountException(String msg) {
		super(msg);
	}
	
	

}
