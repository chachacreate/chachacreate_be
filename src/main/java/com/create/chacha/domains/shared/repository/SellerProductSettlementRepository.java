package com.create.chacha.domains.shared.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.create.chacha.domains.shared.entity.seller.SellerSettlementEntity;

/**
 * 상품 정산 메타 조회용 Repository
 * - seller_settlement에서 seller_id 기준으로 상태/수정일을 조회
 */
public interface SellerProductSettlementRepository extends JpaRepository<SellerSettlementEntity, Long> {

    @Query(value = """
            SELECT
                DATE_FORMAT(s.updated_at, '%Y-%m-%d %H:%i:%s') AS updatedAtKey,
                s.updated_at                                   AS updatedAt,
                s.status                                       AS settlementStatus
            FROM seller_settlement s
            WHERE s.seller_id = :sellerId
            ORDER BY s.updated_at ASC
            """, nativeQuery = true)
    List<SellerSettlementRow> findAllMetaBySellerId(@Param("sellerId") Long sellerId);

    interface SellerSettlementRow {
        String getUpdatedAtKey();   
        Timestamp getUpdatedAt();   
        Integer getSettlementStatus();
    }
}
