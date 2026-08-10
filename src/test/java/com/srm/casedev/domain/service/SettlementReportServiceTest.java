package com.srm.casedev.domain.service;

import com.srm.casedev.api.dto.settlement.SettlementReportProjection;
import com.srm.casedev.domain.repository.SettlementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementReportServiceTest {

    @Mock
    private SettlementRepository settlementRepository;

    @InjectMocks
    private SettlementReportService settlementReportService;

    private Page<SettlementReportProjection> page;

    @BeforeEach
    void setUp() {
        page = new PageImpl<>(
                Collections.emptyList()
        );
    }

    @Test
    void shouldReturnSettlementReport() {

        when(settlementRepository.findSettlementReport(
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(page);

        Page<SettlementReportProjection> result =
                settlementReportService.findSettlementReport(
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 1, 31),
                        "ACME",
                        "USD",
                        0,
                        20
                );

        assertEquals(page, result);

        verify(settlementRepository).findSettlementReport(
                eq(LocalDate.of(2026, 1, 1).atStartOfDay()),
                eq(LocalDate.of(2026, 2, 1).atStartOfDay()),
                eq("ACME"),
                eq("USD"),
                any(Pageable.class)
        );
    }

    @Test
    void shouldNormalizeAssignorAndCurrency() {

        when(settlementRepository.findSettlementReport(
                any(),
                any(),
                eq("ACME"),
                eq("USD"),
                any(Pageable.class)
        )).thenReturn(page);

        Page<SettlementReportProjection> result =
                settlementReportService.findSettlementReport(
                        null,
                        null,
                        "  ACME  ",
                        "  USD  ",
                        0,
                        20
                );

        assertEquals(page, result);

        verify(settlementRepository).findSettlementReport(
                isNull(),
                isNull(),
                eq("ACME"),
                eq("USD"),
                any(Pageable.class)
        );
    }

    @Test
    void shouldConvertStartDateToStartOfDay() {

        when(settlementRepository.findSettlementReport(
                any(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(page);

        LocalDate startDate =
                LocalDate.of(2026, 5, 10);

        settlementReportService.findSettlementReport(
                startDate,
                null,
                null,
                null,
                0,
                20
        );

        verify(settlementRepository).findSettlementReport(
                eq(startDate.atStartOfDay()),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldConvertEndDateToNextDayStartOfDay() {

        when(settlementRepository.findSettlementReport(
                isNull(),
                any(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(page);

        LocalDate endDate =
                LocalDate.of(2026, 5, 10);

        settlementReportService.findSettlementReport(
                null,
                endDate,
                null,
                null,
                0,
                20
        );

        verify(settlementRepository).findSettlementReport(
                isNull(),
                eq(endDate.plusDays(1).atStartOfDay()),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldAllowStartDateWithoutEndDate() {

        when(settlementRepository.findSettlementReport(
                any(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(page);

        LocalDate startDate =
                LocalDate.of(2026, 5, 10);

        Page<SettlementReportProjection> result =
                settlementReportService.findSettlementReport(
                        startDate,
                        null,
                        null,
                        null,
                        0,
                        20
                );

        assertEquals(page, result);

        verify(settlementRepository).findSettlementReport(
                eq(startDate.atStartOfDay()),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldAllowEndDateWithoutStartDate() {

        when(settlementRepository.findSettlementReport(
                isNull(),
                any(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(page);

        LocalDate endDate =
                LocalDate.of(2026, 5, 10);

        Page<SettlementReportProjection> result =
                settlementReportService.findSettlementReport(
                        null,
                        endDate,
                        null,
                        null,
                        0,
                        20
                );

        assertEquals(page, result);

        verify(settlementRepository).findSettlementReport(
                isNull(),
                eq(endDate.plusDays(1).atStartOfDay()),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldRejectNegativePage() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> settlementReportService.findSettlementReport(
                                null,
                                null,
                                null,
                                null,
                                -1,
                                20
                        )
                );

        assertEquals(
                "Page must be greater than or equal to zero",
                exception.getMessage()
        );

        verify(
                settlementRepository,
                never()
        ).findSettlementReport(
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldRejectZeroSize() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> settlementReportService.findSettlementReport(
                                null,
                                null,
                                null,
                                null,
                                0,
                                0
                        )
                );

        assertEquals(
                "Size must be greater than zero",
                exception.getMessage()
        );

        verify(
                settlementRepository,
                never()
        ).findSettlementReport(
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldRejectNegativeSize() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> settlementReportService.findSettlementReport(
                                null,
                                null,
                                null,
                                null,
                                0,
                                -1
                        )
                );

        assertEquals(
                "Size must be greater than zero",
                exception.getMessage()
        );

        verify(
                settlementRepository,
                never()
        ).findSettlementReport(
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldRejectSizeGreaterThan100() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> settlementReportService.findSettlementReport(
                                null,
                                null,
                                null,
                                null,
                                0,
                                101
                        )
                );

        assertEquals(
                "Size must not be greater than 100",
                exception.getMessage()
        );

        verify(
                settlementRepository,
                never()
        ).findSettlementReport(
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldRejectWhenStartDateIsAfterEndDate() {

        LocalDate startDate =
                LocalDate.of(2026, 5, 20);

        LocalDate endDate =
                LocalDate.of(2026, 5, 10);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> settlementReportService.findSettlementReport(
                                startDate,
                                endDate,
                                null,
                                null,
                                0,
                                20
                        )
                );

        assertEquals(
                "Start date must not be after end date",
                exception.getMessage()
        );

        verify(
                settlementRepository,
                never()
        ).findSettlementReport(
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldNormalizeBlankAssignorAndCurrencyToNull() {

        when(settlementRepository.findSettlementReport(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(page);

        Page<SettlementReportProjection> result =
                settlementReportService.findSettlementReport(
                        null,
                        null,
                        "   ",
                        "",
                        0,
                        20
                );

        assertEquals(page, result);

        verify(settlementRepository).findSettlementReport(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldAcceptMaximumPageSize() {

        when(settlementRepository.findSettlementReport(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(page);

        Page<SettlementReportProjection> result =
                settlementReportService.findSettlementReport(
                        null,
                        null,
                        null,
                        null,
                        0,
                        100
                );

        assertEquals(page, result);

        verify(settlementRepository).findSettlementReport(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }
}