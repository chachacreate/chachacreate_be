package com.create.chacha.domains.seller.areas.settlement.service;

import java.util.List;

import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassDailySettlementResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassOptionResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.StoreMonthlySettlementItemDTO;

/**
 * 판매자 정산 서비스
 *
 * - storeUrl을 입력받아 내부에서 Legacy 연동( storeId/sellerId 해석 ) 수행
 * - Repository는 우리(MySQL)만 조회, Legacy 메타(계좌/은행/상태 주인)는 여기서 합성
 */
public interface SellerSettlementService {

    /** [드롭다운] 스토어의 클래스 목록 (storeUrl 기반) */
    List<ClassOptionResponseDTO> getClassOptionsByStore(String storeUrl);

    /** [상세] 특정 클래스 일별 정산 (storeUrl + classId) */
    ClassDailySettlementResponseDTO getDailySettlementByClass(String storeUrl, Long classId);

    /**
     * [월별] 스토어 전체 월별 정산
     * @param storeUrl           PathVariable의 storeUrl
     * @param accountHolderName  ✅ 토큰의 사용자 이름 (예금주명으로 사용)
     */
    List<StoreMonthlySettlementItemDTO> getMonthlySettlementsByStore(String storeUrl, String accountHolderName);
}
