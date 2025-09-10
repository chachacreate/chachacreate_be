package com.create.chacha.domains.seller.areas.settlement.controller;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassSalesResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.service.SellerSettlementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 클래스 매출 조회 API
 */
@Slf4j
@RestController
@RequestMapping("/api/seller/sales/{storeUrl}")
@RequiredArgsConstructor
public class ClassSalesController {

    private final SellerSettlementService service;

    /**
     * 특정 스토어의 클래스 일별 매출 조회
     * @param storeUrl 
     * @return 매출 리스트 (날짜별 금액)
     */
    @GetMapping("/classes")
    public ApiResponse<List<ClassSalesResponseDTO>> getDailySales(
            @PathVariable("storeUrl") String storeUrl
    ) {
        log.info("[ClassSalesController] 클래스 매출 조회 요청");
        List<ClassSalesResponseDTO> sales = service.getDailySalesByStore(storeUrl);
        return new ApiResponse<>(ResponseCode.SELLER_MAIN_STATUS_OK, sales);
    }
}
