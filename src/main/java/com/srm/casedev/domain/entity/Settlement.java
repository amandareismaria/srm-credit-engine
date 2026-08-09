package com.srm.casedev.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlement")
@Getter
@Setter
@NoArgsConstructor
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "receivable_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_settlement_receivable")
    )
    private Receivable receivable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "payment_currency_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_settlement_payment_currency")
    )
    private Currency paymentCurrency;

    @Column(name = "exchange_rate", precision = 19, scale = 6)
    private BigDecimal exchangeRate;

    @Column(name = "base_rate", precision = 10, scale = 6, nullable = false)
    private BigDecimal baseRate;

    @Column(name = "spread_rate", precision = 10, scale = 6, nullable = false)
    private BigDecimal spreadRate;

    @Column(name = "present_value", precision = 19, scale = 6, nullable = false)
    private BigDecimal presentValue;

    @Column(name = "settled_amount", precision = 19, scale = 6, nullable = false)
    private BigDecimal settledAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private SettlementStatus status;

    @Column(name = "settled_at", nullable = false)
    private LocalDateTime settledAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}