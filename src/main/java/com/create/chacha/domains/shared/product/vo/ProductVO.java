package com.create.chacha.domains.shared.product.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 상품 공통 VO
 * - 여러 도메인(스토어 페이지, 메인 페이지, 관리자 등)에서 공통으로 사용되는 불변 객체
 * - DTO와 달리 API 요청/응답에 종속되지 않고, 엔티티에서 추출한 핵심 정보만 담음
 */
@Getter
@AllArgsConstructor
@Builder
@ToString
public class ProductVO {
    private Long id;              // 상품 ID
    private String name;          // 상품명
    private String categoryName;  // 카테고리명
    private Integer price;        // 상품 가격
    private String thumbnailUrl;  // 썸네일 이미지 URL
    private Double avgRating;     // 평균 평점 (리뷰 조인 시 사용)
}
