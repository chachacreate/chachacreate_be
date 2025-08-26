package com.create.chacha.domains.seller.areas.classes.reservations.service;

import com.create.chacha.domains.seller.areas.classes.reservations.dto.response.SellerClassReservationStatsResponseDTO;

/**
 * 판매자 월별 시간/요일별 예약 통계 서비스
 * - 연도는 현재 연도로 고정(KST)
 * - month 미지정: 당월·어제까지
 * - month 지정: 해당 월 전체
 * - dimension: hour | weekday
 */
public interface SellerClassReservationStatsService {

    /** 스토어 전체 클래스 기준 */
    SellerClassReservationStatsResponseDTO getMonthlyStatsForStore(
            String storeUrl,
            Integer month,
            String dimension
    );

    /** 특정 클래스 기준 (storeUrl + classId) */
    SellerClassReservationStatsResponseDTO getMonthlyStatsForClass(
            String storeUrl,
            Integer month,
            String dimension,
            Integer classInfoId
    );
}
