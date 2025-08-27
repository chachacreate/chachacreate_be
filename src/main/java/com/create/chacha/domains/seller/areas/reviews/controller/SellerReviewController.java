package com.create.chacha.domains.seller.areas.reviews.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewListItemDTO;
import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewStatsResponseDTO;
import com.create.chacha.domains.seller.areas.reviews.service.serviceimpl.SellerReviewQueryServiceImpl;
import com.create.chacha.domains.seller.areas.reviews.service.serviceimpl.SellerReviewStatsServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/{storeUrl}")
public class SellerReviewController {

    private final SellerReviewQueryServiceImpl service;
    private final SellerReviewStatsServiceImpl statsservice;

    @GetMapping("/reviews/stats")
    public ResponseEntity<ApiResponse<ReviewStatsResponseDTO>> getStoreStats(
            @PathVariable("storeUrl") String storeUrl
    ) {
        ReviewStatsResponseDTO body = statsservice.getStoreStats(storeUrl);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, body));
    }

    @GetMapping("/reviews/stats/{productId}")
    public ResponseEntity<ApiResponse<ReviewStatsResponseDTO>> getProductStats(
            @PathVariable("storeUrl") String storeUrl,
            @PathVariable("productId") Long productId
    ) {
        ReviewStatsResponseDTO body = statsservice.getProductStats(storeUrl, productId);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, body));
    }

    @GetMapping("/review")
    public ResponseEntity<ApiResponse<List<ReviewListItemDTO>>> getReviews(
            @PathVariable("storeUrl") String storeUrl
    ) {
        List<ReviewListItemDTO> body = service.getReviewsByStore(storeUrl);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, body));
    }

    @GetMapping("/review/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewListItemDTO>>> getReviewsByProduct(
            @PathVariable("storeUrl") String storeUrl,
            @PathVariable("productId") Long productId
    ) {
        List<ReviewListItemDTO> body = service.getReviewsByStoreAndProduct(storeUrl, productId);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, body));
    }
}
