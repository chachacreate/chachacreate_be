package com.create.chacha.domains.seller.areas.classes.reservationlist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 월간(또는 전체) 클래스 예약 조회 응답 DTO
 * - yearMonth가 null이면 "전체 조회" 의미
 * - items에는 ClassReservationRowDTO 배열이 들어감
 */
@Getter
@Builder
@AllArgsConstructor
public class ClassReservationMonthlyResponseDTO {

    /** 요청 스토어 URL 식별자 */
    private final String storeUrl;

    /** yyyy-MM (없으면 null: 전체 조회) */
    private final String yearMonth;

    /** 예약 목록 */
    private final List<ClassReservationRowDTO> items;
}
