package com.create.chacha.domains.seller.areas.products.productcrud.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductListItemDTO {
    private Long id;
    private String thumbnailUrl;      // 대표이미지(THUMBNAIL 중 sequence 최솟값)
    private String name;
    private Integer price;               // 프로젝트 타입에 맞게 Integer면 Integer로 바꿔도 됨
    private Integer stock;

    private String upCategoryName;
    private String downCategoryName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Boolean isFlagship;       // 대표 상품 여부
    private Boolean isDeleted;        // 삭제 여부
}

