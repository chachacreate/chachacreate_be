package com.create.chacha.domains.seller.areas.settlement.service;


import java.util.List;

import com.create.chacha.domains.seller.areas.settlement.dto.response.SettlementResponseDTO;

/**
 * 판매자 정산 조회 서비스
 *
 * - 입력: memberId 1개
 * - 출력: 정산 목록(정산일자, 정산금액, 계좌번호, 은행명, 예금주명, 정산상태, 최근수정일)
 */
public interface SellerSettlementService {

    /**
     * memberId 하나로 모든 조인/검증을 수행해 정산 정보를 조회한다.
     *
     * @param memberId 회원 식별자
     * @return 정산 응답 DTO 리스트 (최신 정산일자 순)
     */
    List<SettlementResponseDTO> getSettlementsByMemberId(Long memberId);
    List<SettlementResponseDTO> getSettlementsByMemberAndClass(Long memberId, Long classId);
}
