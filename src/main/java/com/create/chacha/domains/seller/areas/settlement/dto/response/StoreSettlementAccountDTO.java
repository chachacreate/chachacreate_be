package com.create.chacha.domains.seller.areas.settlement.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 스토어 정산 계좌 정보 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StoreSettlementAccountDTO {
	private String updatedAtKey;
    private String name;			// 예금주명
    private Integer settlementStatus;		// 정산상태
    private LocalDateTime updateAt;		// 판매자 정산테이블의 최근 수정일 
}
