package com.srm.casedev.controller;

import com.srm.casedev.api.dto.settlement.SettlementReportProjection;
import com.srm.casedev.domain.service.SettlementReportService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/settlements/report")
public class SettlementReportController {

    private final SettlementReportService settlementReportService;

    public SettlementReportController(
            SettlementReportService settlementReportService
    ) {
        this.settlementReportService = settlementReportService;
    }

    @GetMapping
    public Page<SettlementReportProjection> getReport(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            String assignor,

            @RequestParam(required = false)
            String currency,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {

        return settlementReportService.findSettlementReport(
                startDate,
                endDate,
                assignor,
                currency,
                page,
                size
        );
    }
}