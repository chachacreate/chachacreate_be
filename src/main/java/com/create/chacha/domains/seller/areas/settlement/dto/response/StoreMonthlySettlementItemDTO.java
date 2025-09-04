package com.create.chacha.domains.seller.areas.settlement.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * [월별 합계] 스토어 전체 클래스의 월별 정산 응답 한 행
 */
@Getter @Builder @AllArgsConstructor @ToString
public class StoreMonthlySettlementItemDTO {
    private final LocalDateTime settlementDate; // 정산 일자(해당 월의 1일)
    private final Long amount;                 		 // 월별 합계 정산 금액
    private final String account;               		// 계좌 번호(legacySeller.account)
    private final String bank;                  		// 은행명(legacySeller.accountBank)
    private final String name;                  		// 예금주명(토큰의 이름)
    private final Integer status;               		// 정산 상태
    private final LocalDateTime updateAt;       // 최근 수정일
}
