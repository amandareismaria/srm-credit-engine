package com.srm.casedev.domain.service.pricing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultPricingStrategyTest {

    @Test
    void shouldCalculatePresentValueUsingBaseRateAndSpread() {
        DefaultPricingStrategy strategy = new DefaultPricingStrategy();

        BigDecimal faceValue = new BigDecimal("100000");
        BigDecimal baseRate = new BigDecimal("0.01");
        BigDecimal spreadRate = new BigDecimal("0.015");

        BigDecimal result = strategy.calculatePresentValue(
                faceValue,
                baseRate,
                spreadRate,
                6
        );

        BigDecimal expected = faceValue.divide(
                BigDecimal.ONE
                        .add(baseRate)
                        .add(spreadRate)
                        .pow(6),
                6,
                RoundingMode.HALF_UP
        );

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnFaceValueWhenTermIsZero() {
        DefaultPricingStrategy strategy = new DefaultPricingStrategy();

        BigDecimal faceValue = new BigDecimal("100000");
        BigDecimal baseRate = new BigDecimal("0.01");
        BigDecimal spreadRate = new BigDecimal("0.015");

        BigDecimal result = strategy.calculatePresentValue(
                faceValue,
                baseRate,
                spreadRate,
                0
        );

        assertEquals(
                faceValue.setScale(6, RoundingMode.HALF_UP),
                result
        );
    }

    @Test
    void shouldRejectNegativeSpreadRate() {
        DefaultPricingStrategy strategy = new DefaultPricingStrategy();

        BigDecimal faceValue = new BigDecimal("100000");
        BigDecimal baseRate = new BigDecimal("0.01");
        BigDecimal spreadRate = new BigDecimal("-0.015");

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> strategy.calculatePresentValue(
                        faceValue,
                        baseRate,
                        spreadRate,
                        6
                )
        );
    }

    @Test
    void shouldRejectNegativeTerm() {
        DefaultPricingStrategy strategy = new DefaultPricingStrategy();

        BigDecimal faceValue = new BigDecimal("100000");
        BigDecimal baseRate = new BigDecimal("0.01");
        BigDecimal spreadRate = new BigDecimal("0.015");

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> strategy.calculatePresentValue(
                        faceValue,
                        baseRate,
                        spreadRate,
                        -1
                )
        );
    }
}