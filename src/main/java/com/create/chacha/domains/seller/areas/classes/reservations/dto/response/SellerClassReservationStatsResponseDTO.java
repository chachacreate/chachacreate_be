package com.create.chacha.domains.seller.areas.classes.reservations.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 스토어 월별 시간/요일별 예약 건수 응답 DTO
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
public class SellerClassReservationStatsResponseDTO {

    /** 조회 스토어 식별자(URL) */
    private final String storeUrl;

    /** 조회 연/월 */
    private final int year;
    private final int month; // 1~12

    /** 차원: "hour" 또는 "weekday" */
    private final String dimension;

    /** 집계 기간 (반개구간) */
    private final LocalDateTime rangeStart;  // 포함(>=)
    private final LocalDateTime rangeEnd;    // 미포함(<)

    /** 결과 항목 */
    private final List<SellerClassReservationStatsItemDTO> items;

    /** 전체 합계(편의) */
    private final long total;
}
