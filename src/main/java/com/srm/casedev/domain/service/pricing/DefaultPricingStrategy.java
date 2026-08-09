package com.srm.casedev.domain.service.pricing;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class DefaultPricingStrategy implements PricingStrategy {

    private static final MathContext MATH_CONTEXT =
            new MathContext(16, RoundingMode.HALF_UP);

    @Override
    public BigDecimal calculatePresentValue(
            BigDecimal faceValue,
            BigDecimal baseRate,
            BigDecimal spreadRate,
            long termInMonths) {

        if (faceValue == null || faceValue.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Face value must be greater than zero"
            );
        }

        if (baseRate == null || baseRate.signum() < 0) {
            throw new IllegalArgumentException(
                    "Base rate must be greater than or equal to zero"
            );
        }

        if (spreadRate == null || spreadRate.signum() < 0) {
            throw new IllegalArgumentException(
                    "Spread rate must be greater than or equal to zero"
            );
        }

        if (termInMonths < 0) {
            throw new IllegalArgumentException(
                    "Term in months must be greater than or equal to zero"
            );
        }

        BigDecimal rate = BigDecimal.ONE
                .add(baseRate)
                .add(spreadRate);

        BigDecimal discountFactor = rate.pow(
                Math.toIntExact(termInMonths),
                MATH_CONTEXT
        );

        return faceValue.divide(
                discountFactor,
                6,
                RoundingMode.HALF_UP
        );
    }
}