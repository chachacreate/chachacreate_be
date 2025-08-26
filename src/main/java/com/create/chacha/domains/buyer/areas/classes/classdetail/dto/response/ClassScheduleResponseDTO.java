package com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class ClassScheduleResponseDTO {
    private String slot;       // 수업 시간
    private Integer seatsLeft; // 잔여 좌석
    private Boolean reservable;// 예약 가능 여부

    public static ClassScheduleResponseDTO of(Object[] row) {
        return ClassScheduleResponseDTO.builder()
                .slot(row[0].toString())
                .seatsLeft(((Number) row[1]).intValue())
                .reservable(((Number) row[2]).intValue() == 1)
                .build();
    }
}