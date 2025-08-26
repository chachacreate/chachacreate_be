package com.create.chacha.domains.shared.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassInfoEntity, Long> {

    @Query(value = """
        WITH RECURSIVE
        params AS (
            SELECT
                DATE(ci.start_date) AS fromDate,
                DATE(ci.end_date)   AS toDate,
                ci.start_time       AS startTime,
                ci.end_time         AS endTime,
                ci.time_interval    AS intervalMin,
                ci.participant      AS participant,
                TIMESTAMP(DATE(ci.end_date), ci.end_time) AS maxSlot  -- ★ 하드 스톱 경계
            FROM class_info ci
            WHERE ci.id = :classId
        ),
        days(d) AS (
            SELECT fromDate FROM params
            UNION ALL
            SELECT DATE_ADD(d, INTERVAL 1 DAY)
            FROM days, params
            WHERE d < (SELECT toDate FROM params)                          -- ★ 날짜 재귀 상한
        ),
        time_slots AS (
            -- 각 날짜의 첫 슬롯
            SELECT TIMESTAMP(d, (SELECT startTime FROM params)) AS slot,
                   d AS day
            FROM days
            UNION ALL
            -- 동일 day 내에서 interval 분 증가, maxSlot/일일 endTime을 둘 다 넘지 않도록
            SELECT DATE_ADD(ts.slot, INTERVAL (SELECT intervalMin FROM params) MINUTE),
                   ts.day
            FROM time_slots ts, params
            WHERE
              DATE_ADD(ts.slot, INTERVAL (SELECT intervalMin FROM params) MINUTE)
                  <= TIMESTAMP(ts.day, (SELECT endTime FROM params))
              AND DATE_ADD(ts.slot, INTERVAL (SELECT intervalMin FROM params) MINUTE)
                  <= (SELECT maxSlot FROM params)                          -- ★ 전역 하드 스톱
        )
        SELECT
            ts.slot AS slot,
            GREATEST(0, (SELECT participant FROM params) - IFNULL(r.cnt,0)) AS seats_left,
            (ts.slot > NOW()
             AND GREATEST(0, (SELECT participant FROM params) - IFNULL(r.cnt,0)) > 0) AS reservable
        FROM time_slots ts
        LEFT JOIN (
            SELECT reserved_time, COUNT(*) AS cnt
            FROM class_reservation
            WHERE class_info_id = :classId
            GROUP BY reserved_time
        ) r ON r.reserved_time = ts.slot
        WHERE NOT EXISTS (
            SELECT 1
            FROM class_holiday h
            WHERE h.class_info_id = :classId
              AND DATE(h.rest_date) = ts.day
        )
          AND ts.slot <= (SELECT maxSlot FROM params)                      -- ★ 최종 세이프가드
        ORDER BY ts.slot
        """, nativeQuery = true)
    List<Object[]> findClassSchedule(@Param("classId") Long classId);
}
