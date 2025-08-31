package com.create.chacha.domains.seller.areas.products.productcrud.service;

import com.create.chacha.domains.seller.areas.products.productcrud.dto.request.FlagshipUpdateRequest;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.request.ProductCreateRequestDTO;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.response.FlagshipUpdateResponse;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.response.ProductListItemDTO;

import java.util.List;

public interface SellerProductService {
	
	// 대표 상품 설정/해제
	FlagshipUpdateResponse toggleFlagship(String storeUrl, FlagshipUpdateRequest req);
	 
    // 상품 조회
	List<ProductListItemDTO> getProductsByStoreUrl(String storeUrl);
	
	/** storeUrl 기준으로 seller 연관을 주입해서 다중 상품 생성 */
    List<Long> createProducts(String storeUrl, List<ProductCreateRequestDTO> reqs);
}

