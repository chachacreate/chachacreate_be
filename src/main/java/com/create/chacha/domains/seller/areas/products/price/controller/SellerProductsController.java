package com.create.chacha.domains.seller.areas.products.price.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.seller.areas.products.price.dto.response.ProductPriceRecommendSimpleResponseDTO;
import com.create.chacha.domains.seller.areas.products.price.service.SellerProductPriceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/{storeUrl}/products")
public class SellerProductsController {

    private final SellerProductPriceService sellerProductPriceService;

    /**
     * 상품 가격 추천(모의) — Multipart(이미지 3개)
     * 폼필드: images (3개 파일)
     */
    @PostMapping(
        value = "/price",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ProductPriceRecommendSimpleResponseDTO>> previewPriceByFiles(
            @PathVariable("storeUrl") String storeUrl,
            @RequestPart("images") List<MultipartFile> images
    ) {
        var body = sellerProductPriceService.previewPriceByFiles(storeUrl, images);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, body));
    }
}
