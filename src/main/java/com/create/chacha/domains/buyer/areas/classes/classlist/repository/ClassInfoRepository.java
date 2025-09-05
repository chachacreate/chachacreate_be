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
 * - 메인홈 전체 조회 + storeId 기반 특정 스토어 조회를 하나의 메서드로 통합 - storeId 파라미터가 NULL → 전체 조회
 * - storeId 값이 있으면 해당 스토어만 조회 - 썸네일 정책: imageSequence = 1 (대표 1장)
 */
public interface ClassInfoRepository extends JpaRepository<ClassInfoEntity, Long> {

	/**
	 * 목록/검색 + 스토어별 조회 + 페이지네이션
	 * 
	 * @param storeId null 이면 전체 조회, 값이 있으면 해당 스토어 조회
	 * @param keyword 검색어(클래스 제목 LIKE)
	 */
	// ⚠️ store 테이블 조인 없음. class_info에서 FK만 읽음.
	@Query(value = "SELECT ci.store_id FROM class_info ci WHERE ci.id = :classId", nativeQuery = true)
	Optional<Long> findStoreIdByClassId(@Param("classId") Long classId);

	// storeName 필요
	@Query(value = """
			SELECT new com.create.chacha.domains.shared.classes.vo.ClassCardVO(
			    ci.id,
			    ci.title,
			    img.url,
			    ci.store.id,
			    ci.addressRoad,
			    ci.price,
			    (ci.participant - COUNT(r.id)),
			    ci.startDate,
			    ci.endDate
			)
			FROM ClassInfoEntity ci
			JOIN com.create.chacha.domains.shared.entity.classcore.ClassImageEntity img
			     ON img.classInfo.id = ci.id
			    AND img.imageSequence = 1
			    AND img.id = (
			        SELECT MIN(i2.id)
			        FROM com.create.chacha.domains.shared.entity.classcore.ClassImageEntity i2
			        WHERE i2.classInfo.id = ci.id
			          AND i2.imageSequence = 1
			          AND i2.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL
			    )
			LEFT JOIN com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity r
			       ON r.classInfo.id = ci.id
			      AND r.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
			WHERE ci.isDeleted = false
			  AND (:storeId IS NULL OR ci.store.id = :storeId)
			  AND (:keyword IS NULL OR ci.title LIKE CONCAT('%', :keyword, '%'))
			GROUP BY ci.id, ci.title, img.url,
			         ci.addressRoad, ci.price, ci.participant,
			         ci.startDate, ci.endDate
			""", countQuery = """
			SELECT COUNT(ci.id)
			FROM ClassInfoEntity ci
			WHERE ci.isDeleted = false
			  AND (:storeId IS NULL OR ci.store.id = :storeId)
			  AND (:keyword IS NULL OR ci.title LIKE CONCAT('%', :keyword, '%'))
			""")
	List<ClassCardVO> findClassCards(@Param("storeId") Long storeId, @Param("keyword") String keyword,
			Pageable pageable);

	/**
	 * 총 개수 카운트 (페이지네이션 메타)
	 */
	@Query("""
			SELECT COUNT(DISTINCT ci.id)
			FROM ClassInfoEntity ci
			JOIN ClassImageEntity img
			     ON img.classInfo.id = ci.id
			    AND img.imageSequence = 1
			    AND img.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL
			WHERE ci.isDeleted = false
			  AND (:storeId IS NULL OR ci.store.url = :storeId)
			  AND (:keyword IS NULL OR ci.title LIKE CONCAT('%', :keyword, '%'))
			       """)
	long countClassCards(@Param("storeId") Long storeId, @Param("keyword") String keyword);

	/**
	 * 날짜 기준 "예약 가능" 클래스 조회 - storeId NULL → 전체 클래스 - storeId 값 존재 → 해당 스토어만 - 대상 날짜
	 * 구간: [start, end) - 휴무 제외 + 좌석 남은 경우만
	 */

	// storeName 필요
	@Query("""
			SELECT new com.create.chacha.domains.shared.classes.vo.ClassCardVO(
			    ci.id,
			    ci.title,
			    img.url,
			    ci.store.id,
			    ci.addressRoad,
			    ci.price,
			    (ci.participant - COUNT(r.id)),
			    ci.startDate,
			    ci.endDate
			)
			FROM ClassInfoEntity ci
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
			  AND (:storeId IS NULL OR ci.store.id = :storeId)
			  AND :targetDate >= ci.startDate
			  AND :targetDate <= ci.endDate
			  AND NOT EXISTS (
			        SELECT 1
			        FROM com.create.chacha.domains.shared.entity.classcore.ClassHolidayEntity ch
			        WHERE ch.classInfo.id = ci.id
			          AND ch.isDeleted = false
			          AND ch.restDate >= :start AND ch.restDate < :end
			  )
			GROUP BY ci.id, ci.title, img.url,
			         ci.addressRoad, ci.price, ci.participant,
			         ci.startDate, ci.endDate
			HAVING (ci.participant - COUNT(r.id)) > 0
			ORDER BY ci.startDate ASC
			""")
	List<ClassCardVO> findAvailableClassesByDate(@Param("storeId") Long storeId,
			@Param("targetDate") LocalDateTime targetDate, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	/**
	 * 단건 조회 (store/seller 까지 fetch)
	 */
	@EntityGraph(attributePaths = { "store" })
	@Query("SELECT c FROM ClassInfoEntity c WHERE c.id = :id")
	Optional<ClassInfoEntity> findByClassId(@Param("id") Long id);
}
