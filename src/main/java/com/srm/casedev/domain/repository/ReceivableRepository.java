package com.srm.casedev.domain.repository;

import com.srm.casedev.domain.entity.Receivable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivableRepository extends JpaRepository<Receivable, Long> {
}