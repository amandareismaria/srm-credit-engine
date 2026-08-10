package com.srm.casedev.api.dto.settlement;

import com.srm.casedev.domain.entity.SettlementStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SettlementResponse(
        Long id,
        Long receivableId,
        String paymentCurrency,
        BigDecimal exchangeRate,
        BigDecimal baseRate,
        BigDecimal spreadRate,
        BigDecimal presentValue,
        BigDecimal settledAmount,
        SettlementStatus status,
        LocalDateTime settledAt
) {
}