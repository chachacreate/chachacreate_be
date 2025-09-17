package com.create.chacha.domains.shared.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum;
import com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity;

public interface ClassReservationRepository extends JpaRepository<ClassReservationEntity, String> {

    // JPQL 프로젝션 (컨버터 적용된 평문 name/phone이 들어옵니다)
    interface RowProjection {
        LocalDateTime getReservedTime();
        LocalDateTime getUpdatedTime();
        OrderAndReservationStatusEnum getStatus();
        String getClassTitle();
        String getMemberName();
        String getMemberPhone();
        Integer getPrice();
    }

    // ✅ store 테이블 조인 없이 class_info.storeId로 필터
    // ✅ r.member는 LEFT JOIN: 회원 없는 예약도 안전
    @Query("""
        select 
            r.reservedTime as reservedTime, 
            r.updatedAt    as updatedTime,
            r.status       as status,
            ci.title       as classTitle,
            m.name         as memberName,
            m.phone        as memberPhone,
            ci.price       as price
        from ClassReservationEntity r
            join r.classInfo ci
            left join r.member m
        where ci.storeId = :storeId
          and (:start is null or r.reservedTime >= :start)
          and (:end   is null or r.reservedTime <  :end)
        order by r.reservedTime desc
    """)
    List<RowProjection> findRowsByStoreIdWithOptionalMonth(
            @Param("storeId") Long storeId,
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );
}
