package com.srm.casedev.domain.service;

import com.srm.casedev.domain.entity.*;
import com.srm.casedev.domain.repository.ExchangeRateRepository;
import com.srm.casedev.domain.repository.ReceivableRepository;
import com.srm.casedev.domain.repository.SettlementRepository;
import com.srm.casedev.domain.service.pricing.PricingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.srm.casedev.domain.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class SettlementService {

    private final CurrencyRepository currencyRepository;
    private final ReceivableRepository receivableRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final SettlementRepository settlementRepository;
    private final PricingStrategy pricingStrategy;
    private static final BigDecimal BASE_RATE = new BigDecimal("0.01");

    public SettlementService(
            CurrencyRepository currencyRepository, ReceivableRepository receivableRepository,
            ExchangeRateRepository exchangeRateRepository,
            SettlementRepository settlementRepository,
            PricingStrategy pricingStrategy) {
        this.currencyRepository = currencyRepository;

        this.receivableRepository = receivableRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.settlementRepository = settlementRepository;
        this.pricingStrategy = pricingStrategy;
    }

    @Transactional
    public Settlement settle(
            Long receivableId,
            Long paymentCurrencyId
    ) {

        Receivable receivable = receivableRepository.findById(receivableId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Receivable not found: " + receivableId
                        )
                );
        Currency paymentCurrency = currencyRepository.findById(paymentCurrencyId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payment currency not found: " + paymentCurrencyId
                        )
                );

        BigDecimal exchangeRate = BigDecimal.ONE;

        if (!receivable.getCurrency().getId().equals(paymentCurrency.getId())) {

            ExchangeRate rate = exchangeRateRepository
                    .findTopByFromCurrencyIdAndToCurrencyIdOrderByEffectiveAtDesc(
                            receivable.getCurrency().getId(),
                            paymentCurrency.getId()
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Exchange rate not found"
                            )
                    );

            exchangeRate = rate.getRate();
        }

        BigDecimal spreadRate =
                receivable.getReceivableType().getSpreadRate();

        long termInMonths = ChronoUnit.MONTHS.between(
                LocalDate.now().withDayOfMonth(1),
                receivable.getDueDate().withDayOfMonth(1)
        );

        termInMonths = Math.max(termInMonths, 1);

        BigDecimal presentValue =
                pricingStrategy.calculatePresentValue(
                        receivable.getFaceValue(),
                        BASE_RATE,
                        spreadRate,
                        termInMonths
                );
        BigDecimal settledAmount = presentValue.multiply(exchangeRate);

        Settlement settlement = new Settlement();

        settlement.setReceivable(receivable);
        settlement.setPaymentCurrency(paymentCurrency);
        settlement.setExchangeRate(exchangeRate);
        settlement.setBaseRate(BASE_RATE);
        settlement.setSpreadRate(spreadRate);
        settlement.setPresentValue(presentValue);
        settlement.setSettledAmount(settledAmount);
        settlement.setStatus(SettlementStatus.SETTLED);
        settlement.setSettledAt(LocalDateTime.now());
        settlement.setCreatedAt(LocalDateTime.now());

        return settlementRepository.save(settlement);
    }



}