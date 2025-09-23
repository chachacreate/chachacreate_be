package com.create.chacha.domains.seller.areas.store.custom.service;

import com.create.chacha.domains.seller.areas.store.custom.dto.request.StoreCustomUpdateRequestDTO;
import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO;

public interface SellerStoreCustomService {
	
	/** PATCH 업서트: created면 201, 아니면 200 */
    UpsertResult patchUpsert(String storeUrl, StoreCustomUpdateRequestDTO req);
    StoreCustomGetResponseDTO getStoreCustom(String storeUrl);

    @lombok.Value
    class UpsertResult {
        boolean created;
        StoreCustomGetResponseDTO body;
    }
}
