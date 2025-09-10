package com.create.chacha.domains.shared.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity;

import java.util.List;

/**
 * 클래스 매출 Repository
 * - class_reservation 과 class_info 테이블을 조인
 * - 조건:
 *   1. 특정 store_id 의 클래스만 조회
 *   2. 예약 상태(status)가 'ORDER_OK'인 건만 포함
 * - 결과: 예약일자별로 매출 합계를 집계
 */
@Repository
public interface ClassSalesRepository extends JpaRepository<ClassReservationEntity, String> {

    @Query(value = """
        SELECT 
            DATE(cr.reserved_time) AS ymd,
            IFNULL(SUM(ci.price), 0) AS amt
        FROM class_reservation cr
        JOIN class_info ci ON cr.class_info_id = ci.id
        WHERE ci.store_id = :storeId
          AND cr.status = 'ORDER_OK'
        GROUP BY DATE(cr.reserved_time)
        ORDER BY ymd ASC
        """, nativeQuery = true)
    List<Object[]> findDailySalesByStore(@Param("storeId") Long storeId);
}


