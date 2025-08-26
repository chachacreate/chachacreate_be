package com.create.chacha.domains.seller.areas.classes.reservations.service;

import com.create.chacha.domains.seller.areas.classes.reservations.dto.response.SellerClassReservationStatsResponseDTO;

/**
 * 판매자 월별 시간/요일별 예약 통계 서비스
 * - 연도는 현재 연도로 고정
 * - 월/차원만 선택
 */
public interface SellerClassReservationStatsService {

    /**
     * 스토어 월별 통계(시간별/요일별)
     *
     * @param storeUrl  스토어 URL (store.url)
     * @param month     조회 월(1~12, null이면 당월)
     * @param dimension "hour" | "weekday" (null/공백 → hour)
     */
    SellerClassReservationStatsResponseDTO getMonthlyStatsForStore(
            String storeUrl,
            Integer month,
            String dimension
    );
}
