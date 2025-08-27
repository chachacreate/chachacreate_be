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
 * - DB에 접근하여 "목록 카드에 필요한 필드만" 조회 - Entity → VO 로 바로 투영(프로젝션)해서, API 응답에 맞춘 최소
 * 데이터만 꺼내온다.
 *
 * ─ 설계 포인트 - JPQL "생성자 표현식"을 사용: SELECT new FQCN(…) - 썸네일 정책: imageSequence = 1
 * 을 대표 이미지로 사용 (상태값 정책으로 바꾸면 조건만 교체) - Pageable 을 파라미터로 받아, Service 에서 넘어온
 * page/size/sort 를 그대로 적용한다.
 */
public interface ClassInfoRepository extends JpaRepository<ClassInfoEntity, Long> {

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
		    JOIN StoreEntity st ON ci.store.id = st.id
		    JOIN ClassImageEntity img 
		         ON img.classInfo.id = ci.id
		        AND img.imageSequence = 1
		        AND img.id = (
		            SELECT MIN(i2.id)
		            FROM ClassImageEntity i2
		            WHERE i2.classInfo.id = ci.id
		              AND i2.imageSequence = 1
		        )
		    LEFT JOIN ClassReservationEntity r ON ci.id = r.classInfo.id
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
	 * 총 개수 카운트 (페이지네이션 메타 계산용)
	 */
	@Query("""
			SELECT COUNT(ci.id)
			FROM ClassInfoEntity ci
			WHERE ci.isDeleted = false
			  AND (:keyword IS NULL OR ci.title LIKE CONCAT('%', :keyword, '%'))
			""")
	long countClassCards(@Param("keyword") String keyword);

	/**
	 * 날짜 기준 예약 가능 클래스 조회
	 * - [start, end) 반열림 구간으로 기간 교집합 검색
	 * - 대표 이미지 1장만 보장(imageSequence=1 중 id 최솟값)
	 * - 남은 좌석 = participant - COUNT(r.id)
	 * - 시작일시 기준 오름차순
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
	    JOIN StoreEntity st ON ci.store.id = st.id
	    JOIN ClassImageEntity img
	         ON img.classInfo.id = ci.id
	        AND img.imageSequence = 1
	        AND img.id = (
	            SELECT MIN(i2.id)
	            FROM ClassImageEntity i2
	            WHERE i2.classInfo.id = ci.id
	              AND i2.imageSequence = 1
	        )
	    LEFT JOIN ClassReservationEntity r
	           ON r.classInfo.id = ci.id
	    WHERE ci.isDeleted = false
	      AND ci.startDate < :end
	      AND ci.endDate   >= :start
	    GROUP BY ci.id, ci.title, img.url, st.name,
	             ci.addressRoad, ci.price, ci.participant,
	             ci.startDate, ci.endDate
	    ORDER BY ci.startDate ASC
	    """)
	List<ClassCardVO> findAvailableClassesByDate(
	        @Param("start") LocalDateTime start,
	        @Param("end")   LocalDateTime end
	);


	// store/seller/member까지 필요하면 EntityGraph로 기본 fetch 최적화
	@EntityGraph(attributePaths = { "store" })
	@Query("select c from ClassInfoEntity c where c.id = :id")
	Optional<ClassInfoEntity> findByclassId(@Param("id") Long id);

}
