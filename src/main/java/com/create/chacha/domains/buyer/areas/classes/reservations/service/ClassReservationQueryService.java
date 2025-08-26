package com.create.chacha.domains.buyer.areas.classes.reservations.service;

import com.create.chacha.domains.buyer.areas.classes.reservations.dto.response.ClassReservationSummaryResponseDTO;

import java.util.List;


/**
 * 예약 조회 비즈니스 서비스
 */
public interface ClassReservationQueryService {
	/**
     * 특정 회원의 클래스 예약 목록 전체 조회(최신순)
     *
     * @param memberId 회원 ID(양수 필수)
     * @return 예약 요약 DTO 목록(정렬: createdAt DESC)
     */
    List<ClassReservationSummaryResponseDTO> getReservationsByMember(Long memberId);
}
