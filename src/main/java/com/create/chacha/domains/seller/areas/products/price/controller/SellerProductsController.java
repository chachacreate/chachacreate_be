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
        // 1) 개수 검증 (정확히 3장)
        if (images == null || images.size() != 3) {
            return ResponseEntity
                    .status(ResponseCode.SELLER_PRICE_PREVIEW_BAD_REQUEST.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_PRICE_PREVIEW_BAD_REQUEST, null));
        }

        // 2) 파일 유효성 검증 (빈 파일 X, image/* 만 허용)
        for (MultipartFile f : images) {
            if (f == null || f.isEmpty()) {
                return ResponseEntity
                        .status(ResponseCode.SELLER_PRICE_PREVIEW_BAD_REQUEST.getStatus())
                        .body(new ApiResponse<>(ResponseCode.SELLER_PRICE_PREVIEW_BAD_REQUEST, null));
            }
            String ct = f.getContentType();
            if (ct == null || !ct.toLowerCase().startsWith("image/")) {
                return ResponseEntity
                        .status(ResponseCode.SELLER_PRICE_PREVIEW_UNSUPPORTED.getStatus())
                        .body(new ApiResponse<>(ResponseCode.SELLER_PRICE_PREVIEW_UNSUPPORTED, null));
            }
        }

        // 3) 서비스 호출 (계산 전용)
        ProductPriceRecommendSimpleResponseDTO body =
                sellerProductPriceService.previewPriceByFiles(storeUrl, images);

        if (body == null) {
            // 서비스 내부에서 처리 중 오류가 난 경우 (null로 신호)
            return ResponseEntity
                    .status(ResponseCode.SELLER_PRICE_PREVIEW_ERROR.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_PRICE_PREVIEW_ERROR, null));
        }

        // 4) 성공
        return ResponseEntity
                .status(ResponseCode.SELLER_PRICE_PREVIEW_OK.getStatus())
                .body(new ApiResponse<>(ResponseCode.SELLER_PRICE_PREVIEW_OK, body));
    }
}
