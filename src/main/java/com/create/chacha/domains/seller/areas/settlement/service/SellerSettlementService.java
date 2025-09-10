package com.create.chacha.domains.seller.areas.settlement.service;

import java.util.List;

import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassDailySettlementResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassOptionResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassSalesResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.StoreMonthlySettlementItemDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.StoreSettlementAccountDTO;

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
     * [월별] 스토어 클래스 전체 월별 정산
     * @param storeUrl           PathVariable의 storeUrl
     * @param accountHolderName  ✅ 토큰의 사용자 이름 (예금주명으로 사용)
     */
    List<StoreMonthlySettlementItemDTO> getMonthlySettlementsByStore(String storeUrl, String accountHolderName);
    
    /**
     * [월별] 스토어 전체 상품 정산 메타 조회
     * @param storeUrl          PathVariable의 storeUrl
     * @param memberId          JWT 토큰의 memberId
     * @param accountHolderName JWT 토큰의 member.name (예금주명)
     * @return 월별 정산 메타 리스트 (금액/최근수정일은 null; 프론트에서 레거시 응답과 합성)
     */
    List<StoreSettlementAccountDTO> getMonthlyProductsSettlementsByStore(
            String storeUrl,
            String accountHolderName
    );
    
    /**
     * [판매자 정산 메인 페이지]
     * 월별 정산 내역
     * @param storeUrl
     * @param holderName
     * */
    List<StoreMonthlySettlementItemDTO> getMonthlySettlementsByMain(String storeUrl, String holderName);
    
    /**
     * 특정 스토어의 클래스 일별 매출 조회
     * @param storeId 스토어 ID
     * @return 일자별 매출 리스트
     */
    List<ClassSalesResponseDTO> getDailySalesByStore(String storeUrl);
}
