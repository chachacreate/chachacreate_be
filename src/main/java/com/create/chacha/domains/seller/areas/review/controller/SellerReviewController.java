package com.create.chacha.domains.seller.areas.review.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.domains.seller.areas.review.dto.response.ReviewListItemDTO;
import com.create.chacha.domains.seller.areas.review.service.serviceimpl.SellerReviewQueryServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/{storeUrl}")
public class SellerReviewController {

    private final SellerReviewQueryServiceImpl service;

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
