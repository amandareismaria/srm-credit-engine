package com.srm.casedev.domain.repository;
import com.srm.casedev.api.dto.settlement.SettlementReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.srm.casedev.domain.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    boolean existsByReceivableId(Long receivableId);

    @Query(
            value = """
                SELECT
                    s.id AS settlementId,
                    r.id AS receivableId,
                    r.assignor AS assignor,
                    rc.code AS receivableCurrency,
                    pc.code AS paymentCurrency,
                    s.present_value AS presentValue,
                    s.settled_amount AS settledAmount,
                    s.status AS status,
                    s.settled_at AS settledAt
                FROM settlement s
                INNER JOIN receivable r
                    ON r.id = s.receivable_id
                INNER JOIN currency rc
                    ON rc.id = r.currency_id
                INNER JOIN currency pc
                    ON pc.id = s.payment_currency_id
                WHERE
                   (CAST(:startDate AS timestamp) IS NULL
                       OR s.settled_at >= CAST(:startDate AS timestamp))
                   AND (CAST(:endDate AS timestamp) IS NULL
                       OR s.settled_at < CAST(:endDate AS timestamp))
                   AND (CAST(:assignor AS varchar) IS NULL
                       OR r.assignor = CAST(:assignor AS varchar))
                   AND (CAST(:currency AS varchar) IS NULL
                       OR pc.code = CAST(:currency AS varchar))
                ORDER BY s.settled_at DESC
                """,
            countQuery = """
    SELECT COUNT(s.id)
    FROM settlement s
    INNER JOIN receivable r
        ON r.id = s.receivable_id
    INNER JOIN currency pc
        ON pc.id = s.payment_currency_id
    WHERE
        (CAST(:startDate AS timestamp) IS NULL
            OR s.settled_at >= CAST(:startDate AS timestamp))
        AND (CAST(:endDate AS timestamp) IS NULL
            OR s.settled_at < CAST(:endDate AS timestamp))
        AND (CAST(:assignor AS varchar) IS NULL
            OR r.assignor = CAST(:assignor AS varchar))
        AND (CAST(:currency AS varchar) IS NULL
            OR pc.code = CAST(:currency AS varchar))
    """
            ,
            nativeQuery = true
    )
    Page<SettlementReportProjection> findSettlementReport(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("assignor") String assignor,
            @Param("currency") String currency,
            Pageable pageable
    );
}