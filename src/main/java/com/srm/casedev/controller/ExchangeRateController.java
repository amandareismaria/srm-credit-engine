package com.srm.casedev.controller;

import com.srm.casedev.api.dto.exchange.ExchangeRateRequest;
import com.srm.casedev.api.dto.exchange.ExchangeRateResponse;
import com.srm.casedev.domain.entity.ExchangeRate;
import com.srm.casedev.domain.service.ExchangeRateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(
            ExchangeRateService exchangeRateService
    ) {
        this.exchangeRateService = exchangeRateService;
    }

    @PostMapping
    public ResponseEntity<ExchangeRateResponse> create(
            @Valid @RequestBody ExchangeRateRequest request
    ) {

        ExchangeRate exchangeRate = exchangeRateService.create(
                request.fromCurrencyId(),
                request.toCurrencyId(),
                request.rate(),
                request.effectiveAt()
        );

        ExchangeRateResponse response = new ExchangeRateResponse(
                exchangeRate.getId(),
                exchangeRate.getFromCurrency().getCode(),
                exchangeRate.getToCurrency().getCode(),
                exchangeRate.getRate(),
                exchangeRate.getEffectiveAt(),
                exchangeRate.getCreatedAt()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}