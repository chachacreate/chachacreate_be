package com.create.chacha.domains.buyer.areas.classes.reservations.repository;

import com.create.chacha.domains.buyer.areas.classes.reservations.dto.response.ClassReservationSummaryResponseDTO;
import com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 예약 조회 전용 Repository
 */
public interface ClassReservationQueryRepository extends JpaRepository<ClassReservationEntity, String> {

    @Query("""
        select new com.create.chacha.domains.buyer.areas.classes.reservations.dto.response.ClassReservationSummaryResponseDTO(
            cr.reservationNumber,
            cr.createdAt,
            ci.title,
            cr.reservedTime,
            cr.status
        )
        from ClassReservationEntity cr
        join cr.classInfo ci
        where cr.member.id = :memberId
        order by cr.createdAt desc
    """)
    List<ClassReservationSummaryResponseDTO> findSummariesByMemberId(@Param("memberId") Long memberId);
}
