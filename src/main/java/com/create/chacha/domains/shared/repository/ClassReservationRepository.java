package com.create.chacha.domains.shared.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum;
import com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity;

/**
 * 클래스 예약 조회 Repository
 */
public interface ClassReservationRepository extends JpaRepository<ClassReservationEntity, String> {

    /**
     * 특정 스토어의 클래스 예약을 월 옵션으로 조회한다.
     *
     * @param storeUrl 스토어 URL (고유 식별자)
     * @param start    월 시작(yyyy-MM-01 00:00:00), 전체 조회 시 null
     * @param end      다음 달 1일 00:00:00, 전체 조회 시 null
     * @return 필요한 칼럼만 담은 프로젝션 목록 (예약일시 DESC)
     *
     */
    @Query("""
        select 
            r.reservedTime as reservedTime, 
            r.updatedAt   as updatedTime,
            r.status      as status,
            ci.title      as classTitle,
            m.name        as memberName,
            m.phone       as memberPhone,
            ci.price      as price
        from ClassReservationEntity r
            join r.classInfo ci
            join ci.store s
            join r.member m
        where s.url = :storeUrl
          and (:start is null or r.reservedTime >= :start)
          and (:end   is null or r.reservedTime <  :end)
        order by r.reservedTime desc
    """)
    List<RowProjection> findRowsByStoreUrlWithOptionalMonth(
            @Param("storeUrl") String storeUrl,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * JPQL 인터페이스 기반 프로젝션
     */
    interface RowProjection {
        LocalDateTime getReservedTime();
        LocalDateTime getUpdatedTime();
        OrderAndReservationStatusEnum getStatus();
        String getClassTitle();
        String getMemberName();
        String getMemberPhone();
        Integer getPrice();
    }
}
