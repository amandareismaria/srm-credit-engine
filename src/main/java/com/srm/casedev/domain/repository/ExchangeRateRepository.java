package com.srm.casedev.domain.repository;

import com.srm.casedev.domain.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findTopByFromCurrencyIdAndToCurrencyIdOrderByEffectiveAtDesc(
            Long fromCurrencyId,
            Long toCurrencyId
    );
}