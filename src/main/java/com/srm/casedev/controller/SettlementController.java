package com.srm.casedev.controller;

import com.srm.casedev.api.dto.settlement.SettlementResponse;
import com.srm.casedev.domain.entity.Settlement;
import com.srm.casedev.domain.service.SettlementService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping
    public ResponseEntity<SettlementResponse> settle(
            @RequestParam Long receivableId,
            @RequestParam Long paymentCurrencyId,
            @RequestParam BigDecimal baseRate
    ) {

        Settlement settlement = settlementService.settle(
                receivableId,
                paymentCurrencyId,
                baseRate
        );

        SettlementResponse response = new SettlementResponse(
                settlement.getId(),
                settlement.getReceivable().getId(),
                settlement.getPaymentCurrency().getCode(),
                settlement.getExchangeRate(),
                settlement.getBaseRate(),
                settlement.getSpreadRate(),
                settlement.getPresentValue(),
                settlement.getSettledAmount(),
                settlement.getStatus(),
                settlement.getSettledAt()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}