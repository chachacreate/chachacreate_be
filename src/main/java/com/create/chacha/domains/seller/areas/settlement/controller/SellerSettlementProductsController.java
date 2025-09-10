package com.create.chacha.domains.seller.areas.settlement.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.common.util.JwtTokenProvider;
import com.create.chacha.config.security.SecurityUser;
import com.create.chacha.domains.seller.areas.settlement.dto.response.StoreSettlementAccountDTO;
import com.create.chacha.domains.seller.areas.settlement.service.SellerSettlementService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/settlements/products/{storeUrl}")
public class SellerSettlementProductsController {

    private final SellerSettlementService service;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 월별 정산 메타(예금주/정산상태/부트 updated_at키) 조회
     * - 결과가 비어도 예금주만 담은 1건을 반환해 프런트에서 표시 가능하게 함
     */
    @GetMapping("/all")
    public ApiResponse<List<StoreSettlementAccountDTO>> getMonthly(
            @AuthenticationPrincipal SecurityUser principal,
            @PathVariable("storeUrl") String storeUrl,
            HttpServletRequest request
    ) {
        // 1) 토큰 → 예금주명(memberName) 추출
        String authHeader = request.getHeader("Authorization");
        String token = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7)
                : null;
        String memberName = (token != null) ? jwtTokenProvider.getName(token) : null;

        log.info("[product-settlement] 메타 조회, storeUrl={}, memberName={}", storeUrl, memberName);

        // 2) 서비스 조회
        List<StoreSettlementAccountDTO> rows =
                service.getMonthlyProductsSettlementsByStore(storeUrl, memberName);

        if (rows == null) {
            // rows 자체가 null인 예외 케이스만 404
            return new ApiResponse<>(ResponseCode.SELLER_SETTLEMENT_NOT_FOUND, null);
        }

        if (rows.isEmpty()) {
            // 정산데이터가 없어도 예금주만 담은 1건 반환
            StoreSettlementAccountDTO nameOnly = StoreSettlementAccountDTO.builder()
                    .updatedAtKey(null)
                    .name(memberName)       	// 예금주
                    .settlementStatus(null) 		// 상태 없음
                    .updateAt(null)         			// updated_at 없음
                    .build();
            return new ApiResponse<>(ResponseCode.SELLER_SETTLEMENT_FOUND, List.of(nameOnly));
        }

        // 정상: 빈 배열도 200(위 분기에서 nameOnly로 대체했으므로 여긴 빈 배열 아님)
        return new ApiResponse<>(ResponseCode.SELLER_SETTLEMENT_FOUND, rows);
    }
}
