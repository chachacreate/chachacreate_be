package com.create.chacha.domains.buyer.areas.classes.reservations.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter@Setter
@ToString
public class ClassReservationPaymentRequestDTO {
    private String id; // ClassReservationEntity 예약 id (UUID)
    private LocalDateTime reservedTime; // 예약할 시간
    private int amount;            // 결제 금액
    private String paymentKey;     // Toss 결제 후 발급된 결제 키
}