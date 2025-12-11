package com.banco.ms.exceptions;

public class ForbiddenOperationException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public ForbiddenOperationException(String msg) {
		super(msg);
	}

}
