package com.create.chacha.domains.seller.areas.settlement.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 판매자 정산 조회 응답 DTO
 * - 정산일, 금액, 계좌, 은행, 예금주, 상태, 최근수정일
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
public class SettlementResponseDTO {
    private final LocalDateTime settlementDate;
    private final Long amount;
    private final String account;
    private final String bank;
    private final String name;
    private final Integer status;
    private final LocalDateTime updateAt;
}
