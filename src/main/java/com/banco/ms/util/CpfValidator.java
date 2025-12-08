package com.banco.ms.util;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<CPF, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext text) {
		if(value == null) return false;
		return CpfUtil.isValid(value);
	}

}
