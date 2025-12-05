package com.banco.ms.dto;

import com.banco.ms.enums.StatusAccount;

public record ContaPfUpdateRequestDto (String name, StatusAccount status) {

}
