package com.create.chacha.domains.seller.areas.settlement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 클래스 일별 매출 응답 DTO
 */
@AllArgsConstructor
@Builder
@Getter
@ToString
public class ClassSalesResponseDTO {
    private LocalDate ymd; // 매출 날짜
    private Integer amt;   // 매출 금액
}
