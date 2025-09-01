package com.create.chacha.domains.seller.areas.products.productcrud.service;

import com.create.chacha.domains.seller.areas.products.productcrud.dto.request.DeleteToggleRequest;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.request.FlagshipUpdateRequest;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.request.ProductCreateRequestDTO;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.request.ProductUpdateDTO;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.response.DeleteToggleResponse;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.response.FlagshipUpdateResponse;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.response.ProductDetailDTO;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.response.ProductListItemDTO;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.response.ProductUpdateResult;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface SellerProductService {
	
	// 상품 삭제 토글
	DeleteToggleResponse toggleDelete(String storeUrl, DeleteToggleRequest request);
	
	// 상품 수정
	ProductUpdateResult updateProductAll(
	        String storeUrl,
	        Long productId,
	        ProductUpdateDTO dto,                   // 기본정보 (null이면 스킵)
	        List<MultipartFile> thumbnails,         // 썸네일 교체 파일들 (null/empty면 스킵)
	        List<Integer> thumbnailSeqs,            // 썸네일 교체 대상 시퀀스들(1..3), 파일 개수와 동일해야 함
	        List<Integer> replaceDescriptionSeqs,   // 설명 이미지 중 교체(삭제마킹)할 시퀀스들
	        List<MultipartFile> descriptions        // 새 설명 이미지들 (append)
	);
	
	// 상품 수정 조회
	ProductDetailDTO getProductForEdit(String storeUrl, Long productId);
	
	// 대표 상품 설정/해제
	FlagshipUpdateResponse toggleFlagship(String storeUrl, FlagshipUpdateRequest req);
	 
    // 상품 조회
	List<ProductListItemDTO> getProductsByStoreUrl(String storeUrl);
	
	/** storeUrl 기준으로 seller 연관을 주입해서 다중 상품 생성 */
    List<Long> createProducts(String storeUrl, List<ProductCreateRequestDTO> reqs);
}

