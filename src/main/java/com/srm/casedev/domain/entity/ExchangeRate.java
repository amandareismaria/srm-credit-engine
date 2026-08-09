package com.srm.casedev.domain.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "exchange_rate",
        indexes = {
                @Index(
                        name = "idx_exchange_rate_currencies",
                        columnList = "from_currency_id, to_currency_id"
                ),
                @Index(
                        name = "idx_exchange_rate_effective_at",
                        columnList = "effective_at"
                )
        }
)
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "from_currency_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_exchange_rate_from_currency")
    )
    private Currency fromCurrency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "to_currency_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_exchange_rate_to_currency")
    )
    private Currency toCurrency;

    @Column(
            name = "rate",
            nullable = false,
            precision = 19,
            scale = 6
    )
    private BigDecimal rate;

    @Column(
            name = "effective_at",
            nullable = false
    )
    private LocalDateTime effectiveAt;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    protected ExchangeRate() {
    }

    public ExchangeRate(
            Currency fromCurrency,
            Currency toCurrency,
            BigDecimal rate,
            LocalDateTime effectiveAt
    ) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.effectiveAt = effectiveAt;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public LocalDateTime getEffectiveAt() {
        return effectiveAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setEffectiveAt(LocalDateTime effectiveAt) {
        this.effectiveAt = effectiveAt;
    }
}