package com.srm.casedev.api.dto.exchange;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRateRequest(

        @NotNull
        Long fromCurrencyId,

        @NotNull
        Long toCurrencyId,

        @NotNull
        @DecimalMin(value = "0.000001")
        BigDecimal rate,

        LocalDateTime effectiveAt
) {
}