package com.create.chacha.domains.buyer.areas.store.mainproduct.service;

import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO;

import java.util.List;

/**
 * 구매자 스토어 메인 - 상품 조회 서비스
 * 
 * 제공 기능:
 *  - 인기상품 조회
 *  - 대표상품 조회
 *  - 전체상품 조회
 */
public interface StoreMainProductService {

    /**
     * 인기상품 조회 (판매량 기준 상위 3개)
     */
    List<ProductResponseDTO> getBestProductsByStore(String storeUrl);

    /**
     * 대표상품 조회 (판매자가 지정한 3개)
     */
    List<ProductResponseDTO> getFlagshipProductsByStore(String storeUrl);

    /**
     * 전체상품 조회 (해당 스토어의 모든 상품)
     */
    List<ProductResponseDTO> getAllProductsByStore(String storeUrl);
}
