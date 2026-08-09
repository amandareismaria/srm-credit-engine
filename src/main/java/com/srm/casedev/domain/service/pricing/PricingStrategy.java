package com.srm.casedev.domain.service.pricing;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePresentValue(
            BigDecimal faceValue,
            BigDecimal baseRate,
            BigDecimal spreadRate,
            long termInMonths
    );
}