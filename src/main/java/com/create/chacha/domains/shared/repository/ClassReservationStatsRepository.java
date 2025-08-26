package com.create.chacha.domains.shared.repository;

import com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ClassReservationStatsRepository extends JpaRepository<ClassReservationEntity, String> {

    interface BucketCountProjection {
        Integer getBucket(); // 시간(0~23) 또는 요일(1~7; MySQL DAYOFWEEK)
        Long getCnt();
    }

    /** 시간별 집계 */
    @Query(value = """
        SELECT HOUR(cr.reserved_time) AS bucket, COUNT(*) AS cnt
        FROM class_reservation cr
        JOIN class_info ci ON cr.class_info_id = ci.id
        JOIN store s       ON ci.store_id = s.id
        WHERE s.url = :storeUrl
          AND cr.status = 'ORDER_OK'
          AND cr.reserved_time >= :start
          AND cr.reserved_time <  :end
          AND (:classInfoId IS NULL OR ci.id = :classInfoId)
        GROUP BY HOUR(cr.reserved_time)
        ORDER BY bucket
        """, nativeQuery = true)
    List<BucketCountProjection> countByHourForStore(
            @Param("storeUrl") String storeUrl,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("classInfoId") Integer classInfoId
    );

    /** 요일별 집계 (1=일 ~ 7=토) */
    @Query(value = """
        SELECT DAYOFWEEK(cr.reserved_time) AS bucket, COUNT(*) AS cnt
        FROM class_reservation cr
        JOIN class_info ci ON cr.class_info_id = ci.id
        JOIN store s       ON ci.store_id = s.id
        WHERE s.url = :storeUrl
          AND cr.status = 'ORDER_OK'
          AND cr.reserved_time >= :start
          AND cr.reserved_time <  :end
          AND (:classInfoId IS NULL OR ci.id = :classInfoId)
        GROUP BY DAYOFWEEK(cr.reserved_time)
        ORDER BY bucket
        """, nativeQuery = true)
    List<BucketCountProjection> countByWeekdayForStore(
            @Param("storeUrl") String storeUrl,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("classInfoId") Integer classInfoId
    );
}
