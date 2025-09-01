package com.create.chacha.domains.buyer.areas.classes.reservations.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.config.security.SecurityUser;
import com.create.chacha.domains.buyer.areas.classes.reservations.dto.response.ClassReservationSummaryResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.reservations.service.ClassReservationQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 구매자-마이페이지: 클래스 예약 조회 API 
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
     * 필터: "전체"(기본), "예정", "지난", "취소"
     * - 숫자도 허용: 1=전체, 2=예정, 3=지난, 4=취소
     * - 영어도 허용: ALL, UPCOMING, PAST, CANCELED
     * 예) /api/mypage/members/reservations?filter=
     * 
     *  * 검색 조회
     * - q: 검색어(주문번호, 클래스명 대상)
     *   예) /api/mypage/members/reservations?q=RV-2025
     */
    @GetMapping("mypage/members/reservations")
    public ApiResponse<List<ClassReservationSummaryResponseDTO>> getMyClassReservations(
            @AuthenticationPrincipal SecurityUser principal,
            @RequestParam(name = "filter", required = false, defaultValue = "전체") String filterRaw,
            @RequestParam(name = "q", required = false) String q
    ) {
    	 if (principal == null || principal.getMemberId() == null) {
             return new ApiResponse<>(ResponseCode.UNAUTHORIZED, null);
         }

         List<ClassReservationSummaryResponseDTO> result =
                 service.getReservationsByMember(principal.getMemberId(), filterRaw, q);

         if (result.isEmpty()) {
             return new ApiResponse<>(ResponseCode.MEMBER_RESERVATIONS_NOT_FOUND, null);
         }
         return new ApiResponse<>(ResponseCode.MEMBER_RESERVATIONS_FOUND, result);
    }
}
