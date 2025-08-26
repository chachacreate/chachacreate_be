package com.create.chacha.domains.seller.areas.store.custom.service.serviceimpl;

import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO;

public interface SellerStoreCustomServiceImpl {
	
	// 스토어 커스텀 조회
	StoreCustomGetResponseDTO getStoreCustom(String storeUrl);
}
