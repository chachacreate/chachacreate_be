package com.create.chacha.domains.seller.areas.classes.reservationlist.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.seller.areas.classes.reservationlist.dto.response.ClassReservationMonthlyResponseDTO;
import com.create.chacha.domains.seller.areas.classes.reservationlist.service.ClassReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 클래스 예약 조회 REST 컨트롤러
 * - yearMonth 미전달 시 전체, 전달 시 해당 월로 필터
 * - 항상 reserved_time DESC 정렬
 *
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ClassReservationController {

    private final ClassReservationService reservationService;

    @GetMapping("/seller/{storeUrl}/classes/reservation")
    public ApiResponse<ClassReservationMonthlyResponseDTO> getReservations(
            @PathVariable("storeUrl") String storeUrl,
            @RequestParam(name = "yearMonth", required = false) String yearMonth
    ) {
    	ClassReservationMonthlyResponseDTO dto = reservationService.getReservations(storeUrl, yearMonth);
    	if(dto == null) {
    		return new ApiResponse<>(ResponseCode.CLASS_RESERVATIONS_NOT_FOUND,null);
    	}
        return new ApiResponse<>(ResponseCode.CLASS_RESERVATIONS_FOUND,dto);
    }
}
