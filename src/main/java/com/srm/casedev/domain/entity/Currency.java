package com.srm.casedev.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "currency",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_currency_code",
                        columnNames = "code"
                )
        }
)
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "code",
            nullable = false,
            length = 3
    )
    private String code;

    @Column(
            name = "name",
            nullable = false,
            length = 50
    )
    private String name;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public Currency() {
    }

    public Currency(String code, String name) {
        this.code = code;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }
}