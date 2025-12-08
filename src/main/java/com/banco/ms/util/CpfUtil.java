package com.banco.ms.util;

public final class CpfUtil {
	
	private CpfUtil() {}
	
	public static boolean isValid(String cpf) {
		
		if(cpf == null) return false;
		
		String onlyDigits = cpf.replace("\\D", "");
		
		if(onlyDigits.length() != 11) return false;
		
		if(onlyDigits.chars().distinct().count() == 1) return false;
		
		int [] digits = new int[11];
		
		for(int i = 0; i < 11; i++) digits[i] = Character.getNumericValue(onlyDigits.charAt(i));
		
		int sum = 0;
		for(int i = 0; i < 9; i++) sum += digits[i] * (10 - i);
		
		int rem = (sum * 10) % 11;
		
		int checkOne = (rem == 10) ? 0 : rem;
		if(checkOne != digits[9]) return false;
		
		sum = 0;
		for(int i = 0; i < 10; i++) sum += digits[i] * (11 - i);
		rem = (sum * 10) % 11;
		int checkTwo = (rem == 10) ? 0 : rem;
		return checkTwo == digits[10];
		
	}

}
