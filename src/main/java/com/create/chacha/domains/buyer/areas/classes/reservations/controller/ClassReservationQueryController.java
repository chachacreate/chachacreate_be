package com.create.chacha.domains.buyer.areas.classes.reservations.controller;

import com.create.chacha.domains.buyer.areas.classes.reservations.dto.response.ClassReservationSummaryResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.reservations.service.ClassReservationQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 구매자-마이페이지: 클래스 예약 조회 API (페이지네이션 제거 버전)
 *
 * - PathVariable: 리소스 소유자(회원) 식별자
 * - 반환 타입: List<DTO> (전체 목록), 정렬은 Repository에서 createdAt DESC 고정
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClassReservationQueryController {

    private final ClassReservationQueryService service;

    /**
     * 특정 회원의 예약 전체 조회(최신순)
     */
    @GetMapping("mypage/members/{memberId}/reservations")
    public List<ClassReservationSummaryResponseDTO> getMyClassReservations(
            @PathVariable("memberId") Long memberId
    ) {
        return service.getReservationsByMember(memberId);
    }
}
