package com.create.chacha.domains.seller.areas.products.productcrud.dto.response;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlagshipUpdateResponse {
    private int totalAfter;          // 변경 후 대표상품 총 개수
    private List<Long> affectedIds;  // 이번 요청으로 실제 변경된 상품 id 목록
}
