package com.banco.ms.util;

public class PixKeyValidator {
	
	public static boolean isValidPixKey(String key) {
		
		if (key == null || key.isBlank()) return false;
		
		if(key.matches("\\d{11}")) return true;
		
		if(key.matches("^\\S+@\\S+\\.\\S+$"))return true;
		
		if(key.matches("^\\+55\\d{10,11}$"))return true;
		
		if(key.matches("^[a-fA-f0-9\\-]{36}$"))return true;
		
		return false;
	}


}
