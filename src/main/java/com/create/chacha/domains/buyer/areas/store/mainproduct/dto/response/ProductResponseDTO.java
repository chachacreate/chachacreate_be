package com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 상품 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
@ToString
public class ProductResponseDTO {
    private Long id;              // 상품 ID
    private String name;          // 상품명
    private String categoryName;  // 카테고리명
    private Integer price;        // 상품 가격
    private String thumbnailUrl;  // 상품 썸네일 이미지 URL
}
