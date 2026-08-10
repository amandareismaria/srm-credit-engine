package com.srm.casedev.domain.repository;

import com.srm.casedev.domain.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    boolean existsByReceivableId(Long receivableId);
}