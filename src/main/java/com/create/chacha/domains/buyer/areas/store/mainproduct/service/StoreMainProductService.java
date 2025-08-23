package com.create.chacha.domains.buyer.areas.store.mainproduct.service;

import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO;

import java.util.List;

/**
 * 구매자 스토어 메인 - 인기상품 조회 서비스
 */
public interface StoreMainProductService {
    /**
     * 특정 스토어의 인기상품 조회 (상위 3개)
     * @param storeId 스토어 ID
     * @return 인기상품 리스트
     */
    List<ProductResponseDTO> getBestProductsByStore(String storeUrl);
}
