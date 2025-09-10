package com.create.chacha.domains.buyer.areas.reviews.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.config.security.SecurityUser;
import com.create.chacha.domains.buyer.areas.reviews.dto.request.ProductReviewRequestDTO;
import com.create.chacha.domains.buyer.areas.reviews.dto.response.ProductReviewResponseDTO;
import com.create.chacha.domains.buyer.areas.reviews.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService reviewService;

    // 상품 리뷰 작성
    @PostMapping
    public ApiResponse<ProductReviewResponseDTO> createReview(@PathVariable Long productId,
                                                              @RequestBody ProductReviewRequestDTO request,
                                                              @AuthenticationPrincipal SecurityUser users) {
        ProductReviewResponseDTO response = reviewService.createReview(productId, request.getRating(),
                                                                        request.getContent(), users.getMemberId());
        return new ApiResponse<>(ResponseCode.CREATED, response);
    }

    // 상품별 리뷰 조회
    @GetMapping
    public ApiResponse<List<ProductReviewResponseDTO>> getReviews(@PathVariable Long productId) {
        List<ProductReviewResponseDTO> reviews = reviewService.getReviewsByProduct(productId);
        return new ApiResponse<>(ResponseCode.OK, reviews);
    }
}