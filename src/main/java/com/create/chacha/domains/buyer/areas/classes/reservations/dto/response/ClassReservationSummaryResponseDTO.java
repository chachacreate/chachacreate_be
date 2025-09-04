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

    // JPQL용 생성자(final로 선언한 변수들만)
    public ClassReservationSummaryResponseDTO(
            Long classId,
            String reservationNumber,
            String image,
            OrderAndReservationStatusEnum status,
            LocalDateTime reservedTime,
            String classTitle,
            String addressRoad,
            Long storeId,
            String displayStatus
    ) {
        this.classId = classId;
        this.reservationNumber = reservationNumber;
        this.image = image;
        this.status = status;
        this.reservedTime = reservedTime;
        this.classTitle = classTitle;
        this.addressRoad = addressRoad;
        this.storeId = storeId;
        this.displayStatus = displayStatus;
    };
}
