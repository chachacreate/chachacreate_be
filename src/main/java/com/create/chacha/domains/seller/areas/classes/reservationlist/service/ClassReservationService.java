package com.create.chacha.domains.seller.areas.classes.reservationlist.service;

import com.create.chacha.domains.seller.areas.classes.reservationlist.dto.response.ClassReservationMonthlyResponseDTO;

/**
 * 클래스 예약 조회 서비스
 * - yearMonth 없으면 전체, 있으면 해당 월로 제한
 */
public interface ClassReservationService {

    /**
     * 특정 스토어의 클래스 예약을 조회한다.
     * @param storeUrl 스토어 URL 식별자
     * @param yearMonthOrNull yyyy-MM 또는 null(전체)
     * @return 월간/전체 예약 응답 DTO
     */
    ClassReservationMonthlyResponseDTO getReservations(String storeUrl, String yearMonthOrNull);
}
