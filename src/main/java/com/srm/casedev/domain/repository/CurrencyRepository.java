package com.srm.casedev.domain.repository;

import com.srm.casedev.domain.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}