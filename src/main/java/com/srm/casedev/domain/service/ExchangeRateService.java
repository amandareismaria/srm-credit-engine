package com.srm.casedev.domain.service;

import com.srm.casedev.domain.entity.Currency;
import com.srm.casedev.domain.entity.ExchangeRate;
import com.srm.casedev.domain.repository.CurrencyRepository;
import com.srm.casedev.domain.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyRepository currencyRepository;

    public ExchangeRateService(
            ExchangeRateRepository exchangeRateRepository,
            CurrencyRepository currencyRepository
    ) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyRepository = currencyRepository;
    }

    @Transactional
    public ExchangeRate create(
            Long fromCurrencyId,
            Long toCurrencyId,
            BigDecimal rate,
            LocalDateTime effectiveAt
    ) {

        if (fromCurrencyId.equals(toCurrencyId)) {
            throw new IllegalArgumentException(
                    "Source and target currencies must be different"
            );
        }

        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Exchange rate must be greater than zero"
            );
        }

        Currency fromCurrency = currencyRepository.findById(fromCurrencyId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Source currency not found: " + fromCurrencyId
                        )
                );

        Currency toCurrency = currencyRepository.findById(toCurrencyId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Target currency not found: " + toCurrencyId
                        )
                );

        if (effectiveAt == null) {
            effectiveAt = LocalDateTime.now();
        }

        ExchangeRate exchangeRate = new ExchangeRate(
                fromCurrency,
                toCurrency,
                rate,
                effectiveAt
        );

        return exchangeRateRepository.save(exchangeRate);
    }
}