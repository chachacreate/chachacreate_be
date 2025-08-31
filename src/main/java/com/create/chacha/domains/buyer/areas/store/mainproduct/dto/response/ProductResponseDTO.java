package com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response;

import com.create.chacha.domains.shared.product.vo.ProductVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 상품 응답 DTO
 * - API 응답을 위한 데이터 구조
 * - 내부적으로 ProductVO를 포함해 공통 구조를 재사용
 */
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class ProductResponseDTO {
    private ProductVO product; // 공통 상품 VO
    
    public static ProductResponseDTO from(ProductVO vo) {
    		return ProductResponseDTO.builder()
    				.product(vo)
    				.build();
    }
}
