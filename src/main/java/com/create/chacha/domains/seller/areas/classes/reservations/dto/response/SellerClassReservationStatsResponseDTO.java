package com.create.chacha.domains.seller.areas.classes.reservations.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 스토어/클래스 월별 시간/요일별 예약 건수 응답 DTO
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
public class SellerClassReservationStatsResponseDTO {
    private final String storeUrl;
    private final Integer classId;   				// class 범위일 때만 값 존재(스토어 전체면 null)
    private final int year;
    private final int month;        				 // 1~12
    private final String dimension;  			// "hour" | "weekday"
    private final LocalDateTime rangeStart;
    private final LocalDateTime rangeEnd;
    private final List<SellerClassReservationStatsItemDTO> items;
    private final long total;
}
