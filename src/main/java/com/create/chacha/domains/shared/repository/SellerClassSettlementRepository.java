package com.create.chacha.domains.shared.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.create.chacha.domains.seller.areas.settlement.repository.SettlementResponseProjection;
import com.create.chacha.domains.shared.entity.member.MemberEntity;

/**
 * 판매자 정산 조회 전용 Repository
 * - Native 쿼리 결과를 Projection으로 반환
 */
@Repository
public interface SellerClassSettlementRepository extends JpaRepository<MemberEntity, Long> {

    /**
     * [전체] 특정 회원(memberId) 소속 판매자의 월별 정산 요약
     * - settlementDate: (ss.created_at + 1개월)의 '20일 00:00:00'
     * - amount: 해당 월의 ORDER_OK 예약 금액 합계
     * - 계좌/은행/판매자명, 상태, 월별 예약 데이터 최종 갱신일
     */
    @Query(value = """
        SELECT
          STR_TO_DATE(
            CONCAT(DATE_FORMAT(DATE_ADD(ss.created_at, INTERVAL 1 MONTH), '%Y-%m'), '-20 00:00:00'),
            '%Y-%m-%d %H:%i:%s'
          ) AS settlementDate,
          COALESCE(a.sum_amount, 0)          AS amount,
          s.account                           AS account,
          s.bank                              AS bank,
          m.name                              AS name,
          ss.status                           AS status,
          a.max_updated_at                    AS updateAt
        FROM member m
        JOIN seller s
          ON s.member_id = m.id
        JOIN seller_settlement ss
          ON ss.seller_id = s.id                            
        LEFT JOIN (
            SELECT
                st.seller_id                              AS seller_id,
                DATE_FORMAT(cr.reserved_time, '%Y-%m-01') AS month_start,
                SUM(ci.price)                             AS sum_amount,
                MAX(cr.updated_at)                        AS max_updated_at
            FROM store st
            JOIN class_info ci
              ON ci.store_id = st.id
             AND IFNULL(ci.is_deleted, 0) = 0
            JOIN class_reservation cr
              ON cr.class_info_id = ci.id
            WHERE IFNULL(st.is_deleted, 0) = 0
              AND cr.status = 'ORDER_OK'
            GROUP BY st.seller_id, DATE_FORMAT(cr.reserved_time, '%Y-%m-01')
        ) a
          ON a.seller_id   = s.id                           
         AND a.month_start = DATE_FORMAT(ss.created_at, '%Y-%m-01')
        WHERE m.id = :memberId
        ORDER BY ss.created_at DESC, ss.id DESC
        """, nativeQuery = true)
    List<SettlementResponseProjection> findSettlementsByMemberId(@Param("memberId") Long memberId);


    /**
     * [특정 클래스] 특정 회원(memberId) + 특정 클래스(classId)의 월별 정산 요약
     * - 상동. 단, 집계 서브쿼리에서 ci.id = :classId 조건 추가
     */
    @Query(value = """
        SELECT
          STR_TO_DATE(
            CONCAT(DATE_FORMAT(DATE_ADD(ss.created_at, INTERVAL 1 MONTH), '%Y-%m'), '-20 00:00:00'),
            '%Y-%m-%d %H:%i:%s'
          ) AS settlementDate,
          COALESCE(a.sum_amount, 0)          AS amount,
          s.account                           AS account,
          s.bank                              AS bank,
          m.name                              AS name,
          ss.status                           AS status,
          a.max_updated_at                    AS updateAt
        FROM member m
        JOIN seller s
          ON s.member_id = m.id
        JOIN seller_settlement ss
          ON ss.seller_id = s.id
        LEFT JOIN (
            SELECT
                st.seller_id                              AS seller_id,
                DATE_FORMAT(cr.reserved_time, '%Y-%m-01') AS month_start,
                SUM(ci.price)                             AS sum_amount,
                MAX(cr.updated_at)                        AS max_updated_at
            FROM store st
            JOIN class_info ci
              ON ci.store_id = st.id
             AND IFNULL(ci.is_deleted, 0) = 0
            JOIN class_reservation cr
              ON cr.class_info_id = ci.id
            WHERE IFNULL(st.is_deleted, 0) = 0
              AND cr.status = 'ORDER_OK'
              AND ci.id = :classId                       
            GROUP BY st.seller_id, DATE_FORMAT(cr.reserved_time, '%Y-%m-01')
        ) a
          ON a.seller_id   = s.id
         AND a.month_start = DATE_FORMAT(ss.created_at, '%Y-%m-01')
        WHERE m.id = :memberId
        ORDER BY ss.created_at DESC, ss.id DESC
        """, nativeQuery = true)
    List<SettlementResponseProjection> findSettlementsByMemberIdAndClassId(
            @Param("memberId") Long memberId,
            @Param("classId")  Long classId
    );
}
