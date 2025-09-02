package com.create.chacha.domains.buyer.areas.classes.reservations.repository;

import com.create.chacha.domains.buyer.areas.classes.reservations.dto.response.ClassReservationSummaryResponseDTO;
import com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClassReservationQueryRepository extends JpaRepository<ClassReservationEntity, String> {

	@Query("""
		    SELECT new com.create.chacha.domains.buyer.areas.classes.reservations.dto.response.ClassReservationSummaryResponseDTO(
		        ci.id, 
		        cr.reservationNumber, 
		        cimg.url, 
		        cr.status, 
		        cr.reservedTime, 
		        ci.title, 
		        ci.addressRoad, 
		        st.name, 
		        st.id, 
		        st.url, 
		        CASE
		            WHEN cr.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
		                 AND cr.reservedTime >= :now
		            THEN 'UPCOMING'
		            WHEN cr.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
		                 AND cr.reservedTime < :now
		            THEN 'PAST'
		            WHEN cr.status IN (
		                 com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.CANCEL_RQ,
		                 com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.CANCEL_OK,
		                 com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.REFUND_RQ,
		                 com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.REFUND_OK
		            )
		            THEN cast(cr.status as string)   
		            ELSE 'OTHER'
		        END
		    )
		    FROM ClassReservationEntity cr
		    JOIN cr.classInfo ci
		    JOIN ci.store st
		    LEFT JOIN com.create.chacha.domains.shared.entity.classcore.ClassImageEntity cimg
		           ON cimg.classInfo = ci
		          AND cimg.imageSequence = 1
		          AND cimg.isDeleted = false
		    WHERE cr.member.id = :memberId
		      AND (
		            :filter = 'ALL'
		         OR (:filter = 'UPCOMING'
		             AND cr.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
		             AND cr.reservedTime >= :now)
		         OR (:filter = 'PAST'
		             AND cr.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
		             AND cr.reservedTime < :now)
		         OR (:filter = 'CANCELED'
		             AND cr.status IN (
		                 com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.CANCEL_RQ,
		                 com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.CANCEL_OK,
		                 com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.REFUND_RQ,
		                 com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.REFUND_OK
		             ))
		      )
		      AND (
		            :kw IS NULL
		         OR lower(cr.reservationNumber) LIKE lower(concat('%', :kw, '%'))
		         OR lower(ci.title) LIKE lower(concat('%', :kw, '%'))
		      )
		    ORDER BY cr.reservedTime DESC, cr.createdAt DESC
		""")
		List<ClassReservationSummaryResponseDTO> findSummariesByMemberAndFilterAndKeyword(
		        @Param("memberId") Long memberId,
		        @Param("now") LocalDateTime now,
		        @Param("filter") String filter,
		        @Param("kw") String keyword
		);



}
