package com.srm.casedev.api.dto.settlement;

import java.math.BigDecimal;

public record SettlementRequest(
        Long receivableId,
        Long paymentCurrencyId,
        BigDecimal baseRate
) {
}