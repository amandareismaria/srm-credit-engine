package com.srm.casedev.api.dto.exchange;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRateResponse(
        Long id,
        String fromCurrency,
        String toCurrency,
        BigDecimal rate,
        LocalDateTime effectiveAt,
        LocalDateTime createdAt
) {
}