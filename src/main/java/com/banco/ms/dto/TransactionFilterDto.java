package com.banco.ms.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TransactionFilterDto(
        @NotNull(message = "Data inicial é obrigatória")
        LocalDateTime startDate,

        @NotNull(message = "Data final é obrigatória")
        LocalDateTime endDate) {
}
