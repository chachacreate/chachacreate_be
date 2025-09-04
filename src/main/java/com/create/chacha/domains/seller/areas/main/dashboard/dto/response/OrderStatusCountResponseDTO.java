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
	
		private long newOrders;      // 신규 주문 (ORDER_OK)
	    private long delivered;      // 배송 완료
	    private long cancelRequests; // 취소 요청
	    private long refunds;        // 환불 완료
}