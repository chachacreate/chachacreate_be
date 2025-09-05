package com.create.chacha.domains.shared.repository;

import com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ClassReservationStatsRepository extends JpaRepository<ClassReservationEntity, String> {

	interface BucketCountProjection {
        Integer getBucket();
        Long getCnt();
    }

    @Query(value = """
        SELECT HOUR(cr.reserved_time) AS bucket, COUNT(*) AS cnt
          FROM class_reservation cr
          JOIN class_info ci ON cr.class_info_id = ci.id
         WHERE ci.store_id = :storeId
           AND cr.status = 'ORDER_OK'
           AND cr.reserved_time >= :start
           AND cr.reserved_time <  :end
           AND (:classInfoId IS NULL OR ci.id = :classInfoId)
         GROUP BY HOUR(cr.reserved_time)
         ORDER BY bucket
        """, nativeQuery = true)
    List<BucketCountProjection> countByHourForStoreId(
            @Param("storeId") Long storeId,                  // ← Long으로 변경
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("classInfoId") Integer classInfoId        // class_info.id 타입에 맞춰 유지/변경
    );

    @Query(value = """
        SELECT DAYOFWEEK(cr.reserved_time) AS bucket, COUNT(*) AS cnt
          FROM class_reservation cr
          JOIN class_info ci ON cr.class_info_id = ci.id
         WHERE ci.store_id = :storeId
           AND cr.status = 'ORDER_OK'
           AND cr.reserved_time >= :start
           AND cr.reserved_time <  :end
           AND (:classInfoId IS NULL OR ci.id = :classInfoId)
         GROUP BY DAYOFWEEK(cr.reserved_time)
         ORDER BY bucket
        """, nativeQuery = true)
    List<BucketCountProjection> countByWeekdayForStoreId(
            @Param("storeId") Long storeId,                  // ← Long으로 변경
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("classInfoId") Integer classInfoId
    );
}
