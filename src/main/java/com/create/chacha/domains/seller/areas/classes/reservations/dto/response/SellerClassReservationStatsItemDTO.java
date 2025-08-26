package com.create.chacha.domains.seller.areas.classes.reservations.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 통계 항목 DTO
 * - bucket: 시간(0~23) 또는 요일(1~7; MySQL DAYOFWEEK 규칙: 1=일, 7=토)
 * - label: 프런트 표시용 라벨("00:00", "MON" 등)
 * - count: 해당 구간 예약 건수
 */
@Getter
@ToString
@AllArgsConstructor
public class SellerClassReservationStatsItemDTO {
    private final int bucket;
    private final String label;
    private final long count;
}
