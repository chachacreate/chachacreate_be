package com.create.chacha.domains.seller.areas.store.custom.service;

import com.create.chacha.domains.seller.areas.store.custom.dto.request.StoreCustomUpdateRequestDTO;
import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO;

public interface SellerStoreCustomService {
	
	// 스토어 커스텀 수정
	StoreCustomGetResponseDTO updateStoreCustom(String storeUrl, StoreCustomUpdateRequestDTO request); // 수정
	
	// 스토어 커스텀 조회
	StoreCustomGetResponseDTO getStoreCustom(String storeUrl);
}
