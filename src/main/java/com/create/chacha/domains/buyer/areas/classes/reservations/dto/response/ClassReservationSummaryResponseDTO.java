package com.create.chacha.domains.buyer.areas.classes.reservations.dto.response;

import com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 구매자 마이페이지 - 클래스 예약 요약 응답 DTO
 */
@Getter
@Builder
@ToString
@AllArgsConstructor
public class ClassReservationSummaryResponseDTO {

    private final String reservationNumber;
    private final LocalDateTime createdAt;
    private final String classTitle;
    private final LocalDateTime reservedTime;
    private final OrderAndReservationStatusEnum status;
}
