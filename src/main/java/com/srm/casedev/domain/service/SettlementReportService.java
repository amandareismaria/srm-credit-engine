package com.srm.casedev.domain.service;

import com.srm.casedev.api.dto.settlement.SettlementReportProjection;
import com.srm.casedev.domain.repository.SettlementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SettlementReportService {

    private final SettlementRepository settlementRepository;

    public SettlementReportService(
            SettlementRepository settlementRepository
    ) {
        this.settlementRepository = settlementRepository;
    }

    @Transactional(readOnly = true)
    public Page<SettlementReportProjection> findSettlementReport(
            LocalDate startDate,
            LocalDate endDate,
            String assignor,
            String currency,
            int page,
            int size
    ) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be greater than or equal to zero"
            );
        }

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "Size must be greater than zero"
            );
        }

        if (size > 100) {
            throw new IllegalArgumentException(
                    "Size must not be greater than 100"
            );
        }

        if (startDate != null
                && endDate != null
                && startDate.isAfter(endDate)) {

            throw new IllegalArgumentException(
                    "Start date must not be after end date"
            );
        }

        LocalDateTime startDateTime = startDate != null
                ? startDate.atStartOfDay()
                : null;

        LocalDateTime endDateTime = endDate != null
                ? endDate.plusDays(1).atStartOfDay()
                : null;

        String normalizedAssignor = normalize(assignor);
        String normalizedCurrency = normalize(currency);

        Pageable pageable = PageRequest.of(page, size);

        return settlementRepository.findSettlementReport(
                startDateTime,
                endDateTime,
                normalizedAssignor,
                normalizedCurrency,
                pageable
        );
    }

    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}