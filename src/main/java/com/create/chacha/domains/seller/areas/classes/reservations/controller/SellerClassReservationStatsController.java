package com.create.chacha.domains.seller.areas.classes.reservations.controller;

import com.create.chacha.domains.seller.areas.classes.reservations.dto.response.SellerClassReservationStatsResponseDTO;
import com.create.chacha.domains.seller.areas.classes.reservations.service.SellerClassReservationStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 판매자 페이지 - 월별 시간/요일별 클래스 예약 건수 통계 API
 *
 * - month 미지정: 당월 "어제까지" 집계
 * - month 지정: 해당 월 전체(다음달 1일 00:00 미만) 집계
 */
@Slf4j
@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerClassReservationStatsController {

    private final SellerClassReservationStatsService statsService;

    @GetMapping("/{storeUrl}/classreservations")
    public SellerClassReservationStatsResponseDTO getStoreMonthlySummary(
            @PathVariable("storeUrl") String storeUrl,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "dimension", required = false, defaultValue = "hour") String dimension
    ) {
        return statsService.getMonthlyStatsForStore(storeUrl, month, dimension);
    }
}
