package com.create.chacha.domains.buyer.areas.classes.reservations.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ClassReservationCompleteResponseDTO {
    // 예약번호, 회원 이름, 클래스 이름, 클래스 시간, 결제 금액
    private String reservationNumber;
    private String memberName;
    private String classTitle;
    private LocalDateTime reservedTime;
    private Integer amount;
}
