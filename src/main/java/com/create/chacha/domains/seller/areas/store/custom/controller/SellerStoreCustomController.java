package com.create.chacha.domains.seller.areas.store.custom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO;
import com.create.chacha.domains.seller.areas.store.custom.service.SellerStoreCustomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/{storeUrl}/store")
public class SellerStoreCustomController {

    private final SellerStoreCustomService sellerStoreCustomService;

    // 스토어 커스텀 조회
    @GetMapping("/custom")
    public ResponseEntity<StoreCustomGetResponseDTO> getCustom(
            @PathVariable("storeUrl") String storeUrl
    ) {
        return ResponseEntity.ok(sellerStoreCustomService.getStoreCustom(storeUrl));
    }
}
