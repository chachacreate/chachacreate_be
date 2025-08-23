package com.create.chacha.domains.buyer.areas.store.mainproduct.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 상품 필터링/검색 요청 DTO
 * - 정렬: latest, order, view, rating, price_low, price_high
 * - 카테고리: categoryType (ucategory|dcategory), categoryId
 * - 검색어: keyword
 */
@Getter
@Setter
@ToString
public class ProductFilterRequestDTO {
    private String sort;          // latest, order, view, rating, price_low, price_high
    private String categoryType;  // ucategory | dcategory
    private Long categoryId;      // 카테고리 ID
    private String keyword;       // 상품명 검색어
}
