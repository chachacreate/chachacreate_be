package com.create.chacha.domains.shared.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.create.chacha.domains.seller.areas.settlement.repository.SettlementResponseProjection;
import com.create.chacha.domains.shared.entity.member.MemberEntity;

/**
 * 판매자 정산 조회 전용 Repository
 * - 도메인 타입은 MemberEntity이지만, 네이티브 쿼리 결과를 Projection으로 조회
 */
@Repository
public interface SellerClassSettlementRepository extends JpaRepository<MemberEntity, Long> {

	
	/**
	 * 판매자 정산 내역 조회 쿼리 
	 *
	 * [조회 대상]
	 * - 특정 회원(memberId)에 연결된 판매자(seller)의 정산 내역(seller_settlement)
	 *
	 * [정산일자 산출 로직]
	 * - settlementDate = (정산 생성일 ss.created_at + 1개월)의 '20일 00:00:00'
	 *   → 예: 8월 생성 건은 9월 20일, 9월 생성 건은 10월 20일
	 *
	 * [조회 컬럼]
	 * - settlementDate   : 정산 예정일
	 * - amount           : 해당 월 예약 금액 합계 (ORDER_OK 상태만 집계, 없으면 0)
	 * - account / bank   : 판매자 계좌 정보
	 * - name             : 판매자명(member.name)
	 * - status           : 정산 상태(seller_settlement.status)
	 * - updateAt         : 해당 월 예약 데이터의 최종 갱신일
	 *
	 * [조인 구조]
	 * - member → seller (회원과 판매자 연결)
	 * - seller → seller_settlement (판매자별 정산 내역)
	 * - store → class_info → class_reservation (예약 금액 합산용 서브쿼리)
	 *
	 * [필터 조건]
	 * - member.id = :memberId (로그인 회원 기준)
	 * - store.is_deleted = 0, class_info.is_deleted = 0 (삭제 제외)
	 * - class_reservation.status = 'ORDER_OK' (정상 예약만 집계)
	 *
	 * [정렬 조건]
	 * - 정산 생성일(ss.created_at) DESC, ss.id DESC (최신 정산 먼저)
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
          ON ss.seller_id = s.member_id
        LEFT JOIN (
    SELECT
        st.seller_id                                AS seller_id,
        DATE_FORMAT(cr.reserved_time, '%Y-%m-01')   AS month_start,
        SUM(ci.price)                                AS sum_amount,
        MAX(cr.updated_at)                           AS max_updated_at
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
  ON a.seller_id  = s.id
 AND a.month_start = DATE_FORMAT(ss.created_at, '%Y-%m-01')
        WHERE m.id = :memberId
        ORDER BY ss.created_at DESC, ss.id DESC
        """, nativeQuery = true)
    List<SettlementResponseProjection> findSettlementsByMemberId(@Param("memberId") Long memberId);
}
