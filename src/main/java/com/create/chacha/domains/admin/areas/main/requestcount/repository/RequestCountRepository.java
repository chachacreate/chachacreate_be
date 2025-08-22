package com.create.chacha.domains.admin.areas.main.requestcount.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.create.chacha.domains.shared.constants.AcceptStatusEnum;
import com.create.chacha.domains.shared.entity.store.StoreEntity;

public interface RequestCountRepository extends JpaRepository<StoreEntity, Long> {

    /**
     * 상태별 스토어 개수 조회
     */
    long countByStatus(AcceptStatusEnum status);

    /**
     * 오늘 신규 스토어 개설 요청 건수
     * (BaseEntity.createdAt을 상속받아 사용한다고 가정)
     */
    @Query("SELECT COUNT(s) FROM StoreEntity s " +
           "WHERE s.status = com.create.chacha.domains.shared.constants.AcceptStatusEnum.PENDING " +
           "AND s.createdAt BETWEEN :start AND :end")
    long countNewStoreRequestsToday(Instant start, Instant end);
}
