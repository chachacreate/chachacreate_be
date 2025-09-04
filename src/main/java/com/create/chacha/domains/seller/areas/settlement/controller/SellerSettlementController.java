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
import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassDailySettlementResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassOptionResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.StoreMonthlySettlementItemDTO;
import com.create.chacha.domains.seller.areas.settlement.service.SellerSettlementService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 판매자 클래스 정산 API
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/settlements/classes/{storeUrl}")
public class SellerSettlementController {

    private final SellerSettlementService service;
    private final JwtTokenProvider jwtTokenProvider;

    /** 드롭다운: 스토어 내 클래스 목록 (id, name) */
    @GetMapping("/class-list")
    public ApiResponse<List<ClassOptionResponseDTO>> getClassOptions(
            @PathVariable("storeUrl") String storeUrl
    ) {
        List<ClassOptionResponseDTO> list = service.getClassOptionsByStore(storeUrl);
        if (list == null || list.isEmpty()) return new ApiResponse<>(ResponseCode.CLASSES_NOT_FOUND, null);
        return new ApiResponse<>(ResponseCode.CLASSES_FOUND, list);
    }

    /** 특정 클래스 정산 상세: 대표이미지 + (일자, 일별금액) + 클래스명 */
    @GetMapping("/{classId}")
    public ApiResponse<ClassDailySettlementResponseDTO> getClassDaily(
            @PathVariable("storeUrl") String storeUrl,
            @PathVariable("classId") Long classId
    ) {
        ClassDailySettlementResponseDTO dto = service.getDailySettlementByClass(storeUrl, classId);
        if (dto == null) return new ApiResponse<>(ResponseCode.SELLER_SETTLEMENT_NOT_FOUND, null);
        return new ApiResponse<>(ResponseCode.SELLER_SETTLEMENT_FOUND, dto);
    }

    /**
     * 스토어 전체 클래스들의 월별 정산
     */
    
    @GetMapping("/all")
    public ApiResponse<List<StoreMonthlySettlementItemDTO>> getMonthly(
            @AuthenticationPrincipal SecurityUser principal,
            @PathVariable("storeUrl") String storeUrl,
            HttpServletRequest request
    ) {
        // 헤더에서 토큰 추출
        String authHeader = request.getHeader("Authorization");
        String token = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7)
                : null;
        // JwtTokenProvider 인스턴스를 통해 이름 클레임 추출
        String holderName = (token != null) ? jwtTokenProvider.getName(token) : null;
        List<StoreMonthlySettlementItemDTO> rows = service.getMonthlySettlementsByStore(storeUrl, holderName);

        if (rows == null || rows.isEmpty()) {
            return new ApiResponse<>(ResponseCode.SELLER_SETTLEMENT_NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.SELLER_SETTLEMENT_FOUND, rows);
    }

}
