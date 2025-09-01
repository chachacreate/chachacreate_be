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
		    select new com.create.chacha.domains.buyer.areas.classes.reservations.dto.response.ClassReservationSummaryResponseDTO(
		        cr.reservationNumber,
		        cimg.url,
		        cr.status,
		        cr.reservedTime,
		        ci.title,
		        ci.addressRoad,
		        st.name,
		        case
		            when cr.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
		                 and cr.reservedTime >= :now
		            then 'UPCOMING'
		            when cr.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
		                 and cr.reservedTime < :now
		            then 'PAST'
		            when cr.status in (
		                com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.CANCEL_RQ,
		                com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.CANCEL_OK,
		                com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.REFUND_RQ,
		                com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.REFUND_OK
		            )
		            then cast(cr.status as string)   
		            else 'OTHER'
		        end
		    )
		    from ClassReservationEntity cr
		    join cr.classInfo ci
		    join ci.store st
		    left join com.create.chacha.domains.shared.entity.classcore.ClassImageEntity cimg
		           on cimg.classInfo = ci
		          and cimg.imageSequence = 1
		          and cimg.isDeleted = false
		    where cr.member.id = :memberId
		      and (
		            :filter = 'ALL'

		         or (:filter = 'UPCOMING'
		             and cr.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
		             and cr.reservedTime >= :now)

		         or (:filter = 'PAST'
		             and cr.status = com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.ORDER_OK
		             and cr.reservedTime < :now)

		         or (:filter = 'CANCELED'
		             and cr.status in (
		                 com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.CANCEL_RQ,
		                 com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.CANCEL_OK,
		                 com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.REFUND_RQ,
		                 com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum.REFUND_OK
		             ))
		      )
		      and (
		            :kw is null
		         or lower(cr.reservationNumber) like :kw
		         or lower(ci.title)            like :kw
		      )
		    order by cr.reservedTime desc, cr.createdAt desc
		""")
		List<ClassReservationSummaryResponseDTO> findSummariesByMemberAndFilterAndKeyword(
		        @Param("memberId") Long memberId,
		        @Param("now") LocalDateTime now,
		        @Param("filter") String filter,
		        @Param("kw") String keyword
		);


}
