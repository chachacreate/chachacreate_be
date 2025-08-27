package com.create.chacha.domains.buyer.areas.classes.reservations.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.config.security.SecurityUser;
import com.create.chacha.domains.buyer.areas.classes.reservations.dto.request.ClassReservationPaymentRequestDTO;
import com.create.chacha.domains.buyer.areas.classes.reservations.service.ClassReservationPaymentService;
import com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{storeUrl}/classes")
public class ClassReservationPaymentController {

    private final ClassReservationPaymentService paymentService;

    @PostMapping("/{classId}/reservations")
    public ApiResponse<String> confirm(@PathVariable Long classId,
                                                       @AuthenticationPrincipal SecurityUser user,
                                                       @RequestBody ClassReservationPaymentRequestDTO request) throws Exception {
        String response = paymentService.payAndSaveReservation(classId, user.getMemberId(), request);
        return new ApiResponse<>(ResponseCode.RESERVATION_SUCCESS, response);
    }
}
