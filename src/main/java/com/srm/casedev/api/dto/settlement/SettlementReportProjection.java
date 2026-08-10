package com.srm.casedev.api.dto.settlement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface SettlementReportProjection {

    Long getSettlementId();

    Long getReceivableId();

    String getAssignor();

    String getReceivableCurrency();

    String getPaymentCurrency();

    BigDecimal getPresentValue();

    BigDecimal getSettledAmount();

    String getStatus();

    LocalDateTime getSettledAt();
}