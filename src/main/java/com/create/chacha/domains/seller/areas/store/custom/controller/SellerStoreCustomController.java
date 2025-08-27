package com.create.chacha.domains.seller.areas.store.custom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.seller.areas.store.custom.dto.request.StoreCustomUpdateRequestDTO;
import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO;
import com.create.chacha.domains.seller.areas.store.custom.service.serviceimpl.SellerStoreCustomServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/{storeUrl}/store")
public class SellerStoreCustomController {

    private final SellerStoreCustomServiceImpl sellerStoreCustomService;

    // 스토어 커스텀 수정 (부분 업데이트)
    @PatchMapping("/custom")
    public ResponseEntity<ApiResponse<StoreCustomGetResponseDTO>> updateCustom(
            @PathVariable("storeUrl") String storeUrl,
            @RequestBody StoreCustomUpdateRequestDTO request
    ) {
        StoreCustomGetResponseDTO body = sellerStoreCustomService.updateStoreCustom(storeUrl, request);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, body));
    }

    // 스토어 커스텀 조회
    @GetMapping("/custom")
    public ResponseEntity<ApiResponse<StoreCustomGetResponseDTO>> getCustom(
            @PathVariable("storeUrl") String storeUrl
    ) {
        StoreCustomGetResponseDTO body = sellerStoreCustomService.getStoreCustom(storeUrl);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, body));
    }
}
