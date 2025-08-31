package com.create.chacha.domains.buyer.areas.classes.classlist.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.create.chacha.domains.shared.classes.vo.ClassCardVO;
import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;

/**
 * 클래스 목록/조건조회/날짜기준 조회 Repository
 *
 * - 목록 카드에 필요한 필드만 VO로 투영
 * - 썸네일 정책: imageSequence = 1 (대표 1장)
 * - Pageable을 파라미터로 받아 service에서 page/size/sort 적용
 */
public interface ClassInfoRepository extends JpaRepository<ClassInfoEntity, Long> {

    /**
     * 목록/검색 + 페이지네이션
     */
    @Query(value = """
        SELECT new com.create.chacha.domains.shared.classes.vo.ClassCardVO(
            ci.id,
            ci.title,
            img.url,
            st.name,
            ci.addressRoad,
            ci.price,
            (ci.participant - COUNT(r.id)),
            ci.startDate,
            ci.endDate
        )
        FROM ClassInfoEntity ci
        JOIN ci.store st
        JOIN com.create.chacha.domains.shared.entity.classcore.ClassImageEntity img
             ON img.classInfo.id = ci.id
            AND img.imageSequence = 1
            AND img.id = (
                SELECT MIN(i2.id)
                FROM com.create.chacha.domains.shared.entity.classcore.ClassImageEntity i2
                WHERE i2.classInfo.id = ci.id
                  AND i2.imageSequence = 1
            )
        LEFT JOIN com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity r
               ON r.classInfo.id = ci.id
              AND r.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
        WHERE ci.isDeleted = false
          AND (:keyword IS NULL OR ci.title LIKE CONCAT('%', :keyword, '%'))
        GROUP BY ci.id, ci.title, img.url, st.name,
                 ci.addressRoad, ci.price, ci.participant,
                 ci.startDate, ci.endDate
        """,
        countQuery = """
        SELECT COUNT(ci.id)
        FROM ClassInfoEntity ci
        WHERE ci.isDeleted = false
          AND (:keyword IS NULL OR ci.title LIKE CONCAT('%', :keyword, '%'))
        """
    )
    List<ClassCardVO> findClassCards(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 총 개수 카운트 (페이지네이션 메타)
     */
    @Query("""
        SELECT COUNT(ci.id)
        FROM ClassInfoEntity ci
        WHERE ci.isDeleted = false
          AND (:keyword IS NULL OR ci.title LIKE CONCAT('%', :keyword, '%'))
        """)
    long countClassCards(@Param("keyword") String keyword);

    /**
     * 날짜 기준 "예약 가능" 클래스 조회
     * - 대상 날짜 구간: [start, end)  (start = YYYY-MM-DDT00:00, end = start + 1day)
     * - 휴무 제외: 해당 일자에 ClassHoliday 존재하면 제외
     * - 남은 좌석: participant - COUNT(해당 일자의 예약) > 0
     * - 대표 썸네일 1장(imageSequence=1 & MIN(id))
     */
    @Query("""
        SELECT new com.create.chacha.domains.shared.classes.vo.ClassCardVO(
            ci.id,
            ci.title,
            img.url,
            st.name,
            ci.addressRoad,
            ci.price,
            (ci.participant - COUNT(r.id)),
            ci.startDate,
            ci.endDate
        )
        FROM ClassInfoEntity ci
        JOIN ci.store st
        JOIN com.create.chacha.domains.shared.entity.classcore.ClassImageEntity img
             ON img.classInfo.id = ci.id
            AND img.imageSequence = 1
            AND img.id = (
                SELECT MIN(i2.id)
                FROM com.create.chacha.domains.shared.entity.classcore.ClassImageEntity i2
                WHERE i2.classInfo.id = ci.id
                  AND i2.imageSequence = 1
            )
        LEFT JOIN com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity r
               ON r.classInfo.id = ci.id
              AND r.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
              AND r.reservedTime >= :start AND r.reservedTime < :end
        WHERE ci.isDeleted = false
          AND :targetDate >= ci.startDate
          AND :targetDate <= ci.endDate
          AND NOT EXISTS (
                SELECT 1
                FROM com.create.chacha.domains.shared.entity.classcore.ClassHolidayEntity ch
                WHERE ch.classInfo.id = ci.id
                  AND ch.isDeleted = false
                  AND ch.restDate >= :start AND ch.restDate < :end
          )
        GROUP BY ci.id, ci.title, img.url, st.name,
                 ci.addressRoad, ci.price, ci.participant,
                 ci.startDate, ci.endDate
        HAVING (ci.participant - COUNT(r.id)) > 0
        ORDER BY ci.startDate ASC
        """)
    List<ClassCardVO> findAvailableClassesByDate(
        @Param("targetDate") LocalDateTime targetDate, // 보통 start(자정) 전달
        @Param("start") LocalDateTime start,           // yyyy-MM-ddT00:00
        @Param("end")   LocalDateTime end              // start + 1 day
    );

    /**
     * 단건 조회 (store/seller 까지 필요하면 fetch 최적화)
     */
    @EntityGraph(attributePaths = { "store" })
    @Query("SELECT c FROM ClassInfoEntity c WHERE c.id = :id")
    Optional<ClassInfoEntity> findByClassId(@Param("id") Long id);
}
