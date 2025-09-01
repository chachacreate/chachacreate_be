package com.create.chacha.domains.seller.areas.products.productcrud.dto.response;

import java.util.List;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeleteToggleResponse {
    private int deletedCount;            // 이번 요청으로 삭제된 개수 (isDeleted=true)
    private int restoredCount;           // 이번 요청으로 복구된 개수 (isDeleted=false)
    private List<Long> affectedIds;      // 실제 토글된 상품 id 목록
}
