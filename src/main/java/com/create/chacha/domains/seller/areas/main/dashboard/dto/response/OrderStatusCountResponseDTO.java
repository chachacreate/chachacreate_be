package com.create.chacha.domains.seller.areas.main.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class OrderStatusCountResponseDTO {
    // 각 주문 상태당 총 개수
    private long totalCount;        // 총 주문수
    private long orderOkCount;      // 신규 주문(주문완료) 수
    private long shippedCount;      // 발송완료 수
    private long deliveredCount;    // 배송완료 슈
	private long cancelRqCount;     // 취소요청 수
    private long cancelOkCount;     // 취소완료 수
    private long refundRqCount;     // 환불요청 수
	private long refundOkCount;     // 환불완료 수
}