package com.create.chacha.domains.buyer.areas.classes.reservations.dto.response;

import com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 구매자 마이페이지 - 클래스 예약 요약 응답 DTO
 */
@Data
@Builder
@AllArgsConstructor
public class ClassReservationSummaryResponseDTO {

    private final Long classId;
    private final String reservationNumber;
    private final String image;
    private final OrderAndReservationStatusEnum status;
    private final LocalDateTime reservedTime;
    private final String classTitle;
    private final String addressRoad;
    private final Long storeId;
    private final String displayStatus;

    private String storeName;  // Legacy API에서 채움
    private String storeUrl;   // Legacy API에서 채움
}
