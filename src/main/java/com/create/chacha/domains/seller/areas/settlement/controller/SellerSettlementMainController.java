package com.create.chacha.domains.seller.areas.settlement.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.common.util.JwtTokenProvider;
import com.create.chacha.domains.seller.areas.settlement.dto.response.StoreMonthlySettlementItemDTO;
import com.create.chacha.domains.seller.areas.settlement.service.SellerSettlementService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/settlements/main/{storeUrl}")
public class SellerSettlementMainController {

    private final SellerSettlementService service;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/all")
    public ApiResponse<List<StoreMonthlySettlementItemDTO>> getMonthly(
            @PathVariable("storeUrl") String storeUrl,
            HttpServletRequest request
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;

        // 토큰 검증에 실패해도 이름은 null로 두고 계속 진행 (목록 조회는 가능해야 함)
        String holderName = null;
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                holderName = jwtTokenProvider.getName(token);
            }
        } catch (Exception ignore) {
            // 만료/무효는 name=null 처리
        }

        final List<StoreMonthlySettlementItemDTO> rows =
                service.getMonthlySettlementsByMain(storeUrl, holderName);

        // 빈 배열이라도 200으로 응답
        return new ApiResponse<>(ResponseCode.SELLER_SETTLEMENT_FOUND, rows);
    }

}
