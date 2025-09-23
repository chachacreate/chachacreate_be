package com.create.chacha.domains.buyer.areas.mainhome.main.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeClassDTO;
import com.create.chacha.domains.buyer.areas.mainhome.main.repository.projection.ClassTimeCountProjection;

public interface MainClassRepository extends JpaRepository<com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity, Long> {


    /**
     * 메인 홈 - 좌석 남은 순(0에 가까운 순) + 양수만 + 진행중(미종료) 클래스
     * 썸네일: image_sequence=1 & status=THUMBNAIL
     */
	// MainClassRepository.java
	@Query("""
		    SELECT new com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeClassDTO(
		        ci.id,
		        ci.title,
		        ci.price,
		        img.url,
		        ci.store.id,
		        null,
		        ci.startDate,
		        ci.endDate,
		        ci.timeInterval,
		        (ci.participant - COUNT(r.id)),
		        ci.participant            
		    )
		    FROM com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity ci
		    JOIN com.create.chacha.domains.shared.entity.classcore.ClassImageEntity img
		         ON img.classInfo.id = ci.id
		        AND img.imageSequence = 1
		        AND img.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL
		    LEFT JOIN com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity r
		           ON r.classInfo.id = ci.id
		          AND r.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
		    WHERE ci.isDeleted = false
		      AND ci.endDate >= CURRENT_TIMESTAMP
		    GROUP BY ci.id, ci.title, img.url, ci.store.id, ci.startDate, ci.endDate,
		             ci.participant, ci.price, ci.timeInterval		    HAVING (ci.participant - COUNT(r.id)) > 0
		    ORDER BY (ci.participant - COUNT(r.id)) ASC
		""")
		List<HomeClassDTO> findClassesOrderByRemainSeatAsc(Pageable pageable);
	
	@Query("""
		    SELECT r.classInfo.id AS classId,
		           FUNCTION('DATE_FORMAT', r.reservedTime, '%H:%i') AS time,
		           COUNT(r.id) AS count
		    FROM com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity r
		    WHERE r.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
		      AND r.classInfo.id IN :classIds
		      AND r.reservedTime >= :start AND r.reservedTime < :end
		    GROUP BY r.classInfo.id, FUNCTION('DATE_FORMAT', r.reservedTime, '%H:%i')
		""")
		List<ClassTimeCountProjection> findTodayTimeCountsByClassIds(
		        @Param("classIds") List<Long> classIds,
		        @Param("start") LocalDateTime start,
		        @Param("end") LocalDateTime end
		);

	
	
	
}
