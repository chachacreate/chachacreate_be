package com.create.chacha.domains.seller.areas.settlement.repository;

import java.time.LocalDateTime;

/**
 * 네이티브 쿼리 결과 인터페이스 기반 프로젝션
 * - 메서드명은 쿼리 컬럼 별칭과 동일해야 함
 */
public interface SettlementResponseProjection {
    LocalDateTime getSettlementDate();
    Long getAmount();
    String getAccount();
    String getBank();
    String getName();
    Integer getStatus();
    LocalDateTime getUpdateAt();
}
