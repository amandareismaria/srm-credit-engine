package com.srm.casedev.domain.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "receivable_type",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_receivable_type_name",
                        columnNames = "name"
                )
        }
)
public class ReceivableType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "name",
            nullable = false,
            length = 50
    )
    private String name;

    @Column(
            name = "spread_rate",
            nullable = false,
            precision = 10,
            scale = 6
    )
    private BigDecimal spreadRate;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    protected ReceivableType() {
    }

    public ReceivableType(String name, BigDecimal spreadRate) {
        this.name = name;
        this.spreadRate = spreadRate;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getSpreadRate() {
        return spreadRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpreadRate(BigDecimal spreadRate) {
        this.spreadRate = spreadRate;
    }
}