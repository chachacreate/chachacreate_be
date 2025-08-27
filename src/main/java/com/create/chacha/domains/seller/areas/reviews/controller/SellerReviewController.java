package com.create.chacha.domains.seller.areas.reviews.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ReviewStatsResponseDTO> getStoreStats(@PathVariable("storeUrl") String storeUrl) {
        return ResponseEntity.ok(statsservice.getStoreStats(storeUrl));
    }

    @GetMapping("/reviews/stats/{productId}")
    public ResponseEntity<ReviewStatsResponseDTO> getProductStats(
            @PathVariable("storeUrl") String storeUrl,
            @PathVariable("productId") Long productId
    ) {
        return ResponseEntity.ok(statsservice.getProductStats(storeUrl, productId));
    }

    @GetMapping("/review")
    public ResponseEntity<List<ReviewListItemDTO>> getReviews(@PathVariable("storeUrl") String storeUrl) {
        return ResponseEntity.ok(service.getReviewsByStore(storeUrl));
    }
    
    @GetMapping("/review/{productId}")
    public ResponseEntity<List<ReviewListItemDTO>> getReviewsByProduct(
            @PathVariable("storeUrl") String storeUrl,
            @PathVariable("productId") Long productId
    ) {
        return ResponseEntity.ok(service.getReviewsByStoreAndProduct(storeUrl, productId));
    }
}
