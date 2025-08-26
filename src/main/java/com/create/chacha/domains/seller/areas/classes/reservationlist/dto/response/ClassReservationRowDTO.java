package com.create.chacha.domains.seller.areas.classes.reservationlist.dto.response;

import com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * 클래스 예약 목록의 한 행 Row DTO
 * - 프론트 요구사항에 맞게 "날짜/시간"을 문자열로 분리 제공
 * - 상태는 Enum 그대로 직렬화(문자열)되도록 유지
 */
@Getter
@Builder
public class ClassReservationRowDTO {

    /** 예약 일자 (yyyy-MM-dd) */
    private final String reservedDate;

    /** 클래스명 */
    private final String className;

    /** 예약 시간 (HH:mm) */
    private final String reservedTime;

    /** 예약자명 */
    private final String reserverName;

    /** 예약자 연락처 */
    private final String reserverPhone;

    /** 결제 금액 (회차 기준: class_info.price) */
    private final Integer paymentAmount;

    /** 예약/주문 상태 (ORDER_OK, CANCEL_RQ, CANCEL_OK, REFUND_RQ, REFUND_OK) */
    private final OrderAndReservationStatusEnum status;

    /** 최근 수정일 (UTC, ISO-8601) */
    private final Instant updatedAt;
}
