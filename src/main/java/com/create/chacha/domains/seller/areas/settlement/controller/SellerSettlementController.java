package com.create.chacha.domains.seller.areas.settlement.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.config.security.SecurityUser;
import com.create.chacha.domains.seller.areas.settlement.dto.response.SettlementResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.service.SellerSettlementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 판매자 정산 조회 API
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sellers")
public class SellerSettlementController {

    private final SellerSettlementService settlementService;

    // 테스트용
    // 예) GET /api/sellers/{memberId}/settlements
//    @GetMapping("/{memberId}/settlements")
//    public ApiResponse<ResponseEntity<List<SettlementResponseDTO>>>  findByMemberIdForNow(
//            @PathVariable("memberId") @NotNull Long memberId) {
//
//        log.debug("[GET] /api/sellers/{}/settlements (NO HEADER AUTH YET)", memberId);
//        List<SettlementResponseDTO> result = settlementService.getSettlementsByMemberId(memberId);
//        return new ApiResponse<>(ResponseCode.OK, ResponseEntity.ok(result));
//    }

    
    // [향후 단계 - 헤더 인증 적용 버전]  ← 지금은 주석 유지
    //
    // - 헤더에 SecurityUser가 담겨 있고, securityUser.getUser().getId() 형태로
    //   memberId를 얻을 수 있다고 가정한 코드.
    // - 보안 연동 완료되면 위 findByMemberIdForNow()를 삭제/주석 처리하고
    //   아래 메서드의 주석을 해제해 사용
    //
    // 예) GET /api/sellers/settlements
    /**
     * 로그인한 판매자의 정산 목록 조회
     *
     * @param principal SecurityUser (헤더 기반 인증 주입)
     * @return ResponseEntity<ApiResponse<List<SettlementResponseDTO>>>
     */
    @GetMapping("/settlements")
    public ApiResponse<List<SettlementResponseDTO>> findMine(
            @AuthenticationPrincipal SecurityUser principal) {

        if (principal == null || principal.getMemberId() == null) {
            log.warn("인증 정보가 없습니다.");
            return new ApiResponse<>(ResponseCode.UNAUTHORIZED, null);
        }

        Long memberId = principal.getMemberId();

        List<SettlementResponseDTO> result = settlementService.getSettlementsByMemberId(memberId);

        if (result.isEmpty()) {
            return new ApiResponse<>(ResponseCode.SELLER_SETTLEMENT_NOT_FOUND, null);
        }

        return new ApiResponse<>(ResponseCode.SELLER_SETTLEMENT_FOUND, result);
    }

}       