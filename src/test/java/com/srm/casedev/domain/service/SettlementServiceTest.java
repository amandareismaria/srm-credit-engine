package com.srm.casedev.domain.service;

import com.srm.casedev.domain.entity.Currency;
import com.srm.casedev.domain.entity.ExchangeRate;
import com.srm.casedev.domain.entity.Receivable;
import com.srm.casedev.domain.entity.ReceivableType;
import com.srm.casedev.domain.entity.Settlement;
import com.srm.casedev.domain.repository.CurrencyRepository;
import com.srm.casedev.domain.repository.ExchangeRateRepository;
import com.srm.casedev.domain.repository.ReceivableRepository;
import com.srm.casedev.domain.repository.SettlementRepository;
import com.srm.casedev.domain.service.pricing.PricingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock
    private ReceivableRepository receivableRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private PricingStrategy pricingStrategy;

    @InjectMocks
    private SettlementService settlementService;

    private Currency brl;
    private Currency usd;
    private Receivable receivable;
    private ReceivableType receivableType;

    @BeforeEach
    void setUp() {
        brl = currency(
                1L,
                "BRL",
                "Brazilian Real"
        );

        usd = currency(
                2L,
                "USD",
                "US Dollar"
        );

        receivableType = new ReceivableType(
                "STANDARD",
                new BigDecimal("0.015")
        );

        receivable = new Receivable();

        receivable.setCurrency(brl);
        receivable.setReceivableType(receivableType);
        receivable.setFaceValue(new BigDecimal("100000"));

        // O SettlementService utiliza o dueDate
        // para calcular o prazo em meses.
        receivable.setDueDate(
                LocalDate.now().plusMonths(6)
        );
    }

    @Test
    void shouldSettleReceivableInSameCurrency() {

        when(receivableRepository.findById(1L))
                .thenReturn(Optional.of(receivable));

        when(currencyRepository.findById(1L))
                .thenReturn(Optional.of(brl));

        when(pricingStrategy.calculatePresentValue(
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                anyLong()
        )).thenReturn(
                new BigDecimal("90000.000000")
        );

        when(settlementRepository.save(any(Settlement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Settlement result = settlementService.settle(
                1L,
                1L,
                new BigDecimal("0.01")
        );

        assertNotNull(result);

        assertEquals(
                new BigDecimal("90000.000000"),
                result.getPresentValue()
        );

        assertEquals(
                new BigDecimal("90000.000000"),
                result.getSettledAmount()
        );

        assertEquals(
                brl,
                result.getPaymentCurrency()
        );

        assertNull(result.getExchangeRate());

        verify(exchangeRateRepository, never())
                .findTopByFromCurrencyIdAndToCurrencyIdOrderByEffectiveAtDesc(
                        anyLong(),
                        anyLong()
                );

        verify(settlementRepository)
                .save(any(Settlement.class));
    }

    @Test
    void shouldSettleReceivableUsingExchangeRateWhenCurrenciesAreDifferent() {

        when(receivableRepository.findById(1L))
                .thenReturn(Optional.of(receivable));

        when(currencyRepository.findById(2L))
                .thenReturn(Optional.of(usd));

        when(pricingStrategy.calculatePresentValue(
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                anyLong()
        )).thenReturn(
                new BigDecimal("90000.000000")
        );

        ExchangeRate exchangeRate = new ExchangeRate(
                brl,
                usd,
                new BigDecimal("5.000000"),
                LocalDateTime.now()
        );

        when(exchangeRateRepository
                .findTopByFromCurrencyIdAndToCurrencyIdOrderByEffectiveAtDesc(
                        1L,
                        2L
                ))
                .thenReturn(Optional.of(exchangeRate));

        when(settlementRepository.save(any(Settlement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Settlement result = settlementService.settle(
                1L,
                2L,
                new BigDecimal("0.01")
        );

        assertNotNull(result);

        assertEquals(
                new BigDecimal("90000.000000"),
                result.getPresentValue()
        );

        assertEquals(
                new BigDecimal("450000.000000"),
                result.getSettledAmount()
        );

        assertEquals(
                new BigDecimal("5.000000"),
                result.getExchangeRate()
        );

        assertEquals(
                usd,
                result.getPaymentCurrency()
        );

        verify(exchangeRateRepository)
                .findTopByFromCurrencyIdAndToCurrencyIdOrderByEffectiveAtDesc(
                        1L,
                        2L
                );

        verify(settlementRepository)
                .save(any(Settlement.class));
    }

    @Test
    void shouldRejectWhenReceivableDoesNotExist() {

        when(receivableRepository.findById(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> settlementService.settle(
                        999L,
                        1L,
                        new BigDecimal("0.01")
                )
        );

        assertEquals(
                "Receivable not found: 999",
                exception.getMessage()
        );

        verify(receivableRepository)
                .findById(999L);

        verify(currencyRepository, never())
                .findById(anyLong());

        verify(settlementRepository, never())
                .save(any(Settlement.class));
    }

    @Test
    void shouldRejectWhenPaymentCurrencyDoesNotExist() {

        when(receivableRepository.findById(1L))
                .thenReturn(Optional.of(receivable));

        when(currencyRepository.findById(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> settlementService.settle(
                        1L,
                        999L,
                        new BigDecimal("0.01")
                )
        );

        assertEquals(
                "Payment currency not found: 999",
                exception.getMessage()
        );

        verify(receivableRepository)
                .findById(1L);

        verify(currencyRepository)
                .findById(999L);

        verify(settlementRepository, never())
                .save(any(Settlement.class));
    }

    @Test
    void shouldRejectWhenExchangeRateDoesNotExist() {

        when(receivableRepository.findById(1L))
                .thenReturn(Optional.of(receivable));

        when(currencyRepository.findById(2L))
                .thenReturn(Optional.of(usd));

        when(pricingStrategy.calculatePresentValue(
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                anyLong()
        )).thenReturn(
                new BigDecimal("90000.000000")
        );

        when(exchangeRateRepository
                .findTopByFromCurrencyIdAndToCurrencyIdOrderByEffectiveAtDesc(
                        1L,
                        2L
                ))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> settlementService.settle(
                        1L,
                        2L,
                        new BigDecimal("0.01")
                )
        );

        assertEquals(
                "Exchange rate not found for BRL to USD",
                exception.getMessage()
        );

        verify(exchangeRateRepository)
                .findTopByFromCurrencyIdAndToCurrencyIdOrderByEffectiveAtDesc(
                        1L,
                        2L
                );

        verify(settlementRepository, never())
                .save(any(Settlement.class));
    }

    @Test
    void shouldRejectWhenReceivableIsAlreadySettled() {

        when(settlementRepository.existsByReceivableId(1L))
                .thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> settlementService.settle(
                        1L,
                        1L,
                        new BigDecimal("0.01")
                )
        );

        assertEquals(
                "Receivable already settled: 1",
                exception.getMessage()
        );

        verify(settlementRepository)
                .existsByReceivableId(1L);

        verify(receivableRepository, never())
                .findById(anyLong());

        verify(currencyRepository, never())
                .findById(anyLong());
    }

    private Currency currency(
            Long id,
            String code,
            String name
    ) {
        Currency currency = new Currency(
                code,
                name
        );

        ReflectionTestUtils.setField(
                currency,
                "id",
                id
        );

        return currency;
    }
}