package com.srm.casedev.domain.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "receivable",
        indexes = {
                @Index(
                        name = "idx_receivable_assignor",
                        columnList = "assignor"
                ),
                @Index(
                        name = "idx_receivable_currency",
                        columnList = "currency_id"
                ),
                @Index(
                        name = "idx_receivable_due_date",
                        columnList = "due_date"
                ),
                @Index(
                        name = "idx_receivable_type",
                        columnList = "receivable_type_id"
                )
        }
)
public class Receivable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "assignor",
            nullable = false,
            length = 150
    )
    private String assignor;

    @Column(
            name = "face_value",
            nullable = false,
            precision = 19,
            scale = 6
    )
    private BigDecimal faceValue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "currency_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_receivable_currency")
    )
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "receivable_type_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_receivable_type")
    )
    private ReceivableType receivableType;

    @Column(
            name = "due_date",
            nullable = false
    )
    private LocalDate dueDate;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    protected Receivable() {
    }

    public Receivable(
            String assignor,
            BigDecimal faceValue,
            Currency currency,
            ReceivableType receivableType,
            LocalDate dueDate
    ) {
        this.assignor = assignor;
        this.faceValue = faceValue;
        this.currency = currency;
        this.receivableType = receivableType;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getAssignor() {
        return assignor;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public Currency getCurrency() {
        return currency;
    }

    public ReceivableType getReceivableType() {
        return receivableType;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setAssignor(String assignor) {
        this.assignor = assignor;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setReceivableType(ReceivableType receivableType) {
        this.receivableType = receivableType;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}