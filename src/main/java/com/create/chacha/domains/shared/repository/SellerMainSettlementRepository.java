package com.create.chacha.domains.shared.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.create.chacha.domains.shared.entity.seller.SellerSettlementEntity;

public interface SellerMainSettlementRepository extends JpaRepository<SellerSettlementEntity, Long> {

    @Query(value = """
        SELECT
            amount        AS amount,
            status        AS status,
            created_at    AS settlementDate,
            updated_at    AS updateAt
        FROM seller_settlement
        WHERE seller_id = :sellerId
        ORDER BY created_at ASC
        """, nativeQuery = true)
    List<Row> findMonthlyRowsBySellerId(@Param("sellerId") Long sellerId);

    interface Row {
        Long getAmount();
        Integer getStatus();
        Timestamp getSettlementDate();
        Timestamp getUpdateAt();
    }
}
