package com.srm.casedev.domain.service;

import com.srm.casedev.domain.entity.Currency;
import com.srm.casedev.domain.entity.ExchangeRate;
import com.srm.casedev.domain.entity.Receivable;
import com.srm.casedev.domain.entity.Settlement;
import com.srm.casedev.domain.entity.SettlementStatus;
import com.srm.casedev.domain.repository.CurrencyRepository;
import com.srm.casedev.domain.repository.ExchangeRateRepository;
import com.srm.casedev.domain.repository.ReceivableRepository;
import com.srm.casedev.domain.repository.SettlementRepository;
import com.srm.casedev.domain.service.pricing.PricingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class SettlementService {

    private final ReceivableRepository receivableRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final SettlementRepository settlementRepository;
    private final CurrencyRepository currencyRepository;
    private final PricingStrategy pricingStrategy;

    public SettlementService(
            ReceivableRepository receivableRepository,
            ExchangeRateRepository exchangeRateRepository,
            SettlementRepository settlementRepository,
            CurrencyRepository currencyRepository,
            PricingStrategy pricingStrategy
    ) {
        this.receivableRepository = receivableRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.settlementRepository = settlementRepository;
        this.currencyRepository = currencyRepository;
        this.pricingStrategy = pricingStrategy;
    }

    @Transactional
    public Settlement settle(
            Long receivableId,
            Long paymentCurrencyId,
            BigDecimal baseRate
    ) {

        if (settlementRepository.existsByReceivableId(receivableId)) {
            throw new IllegalStateException(
                    "Receivable already settled: " + receivableId
            );
        }

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

        BigDecimal spreadRate = receivable
                .getReceivableType()
                .getSpreadRate();

        long termInMonths = Math.max(
                0,
                ChronoUnit.MONTHS.between(
                        LocalDate.now(),
                        receivable.getDueDate()
                )
        );

        BigDecimal presentValue = pricingStrategy.calculatePresentValue(
                receivable.getFaceValue(),
                baseRate,
                spreadRate,
                termInMonths
        );

        BigDecimal exchangeRate = null;
        BigDecimal settledAmount = presentValue;

        boolean crossCurrency =
                !receivable.getCurrency().getId()
                        .equals(paymentCurrency.getId());

        if (crossCurrency) {

            ExchangeRate rate = exchangeRateRepository
                    .findTopByFromCurrencyIdAndToCurrencyIdOrderByEffectiveAtDesc(
                            receivable.getCurrency().getId(),
                            paymentCurrency.getId()
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Exchange rate not found for "
                                            + receivable.getCurrency().getCode()
                                            + " to "
                                            + paymentCurrency.getCode()
                            )
                    );

            exchangeRate = rate.getRate();

            settledAmount = presentValue
                    .multiply(exchangeRate)
                    .setScale(6, RoundingMode.HALF_UP);
        }

        Settlement settlement = new Settlement();

        settlement.setReceivable(receivable);
        settlement.setPaymentCurrency(paymentCurrency);
        settlement.setExchangeRate(exchangeRate);
        settlement.setBaseRate(baseRate);
        settlement.setSpreadRate(spreadRate);
        settlement.setPresentValue(presentValue);
        settlement.setSettledAmount(settledAmount);
        settlement.setStatus(SettlementStatus.SETTLED);
        settlement.setSettledAt(LocalDateTime.now());
        settlement.setCreatedAt(LocalDateTime.now());

        return settlementRepository.save(settlement);
    }
}