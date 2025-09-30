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

    // 상품 리뷰 수정
    @PutMapping("/{reviewId}")
    public ApiResponse<ProductReviewResponseDTO> updateReview(@PathVariable Long productId,
                                                              @PathVariable Long reviewId,
                                                              @RequestBody ProductReviewRequestDTO request,
                                                              @AuthenticationPrincipal SecurityUser user) {
        ProductReviewResponseDTO response =
                reviewService.updateReview(productId, reviewId, request.getRating(), request.getContent(), user.getMemberId());
        return new ApiResponse<>(ResponseCode.OK, response);
    }

    // 상품 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ApiResponse<Boolean> deleteReview(@PathVariable Long productId,
                                             @PathVariable Long reviewId,
                                             @AuthenticationPrincipal SecurityUser user) {
        boolean deleted = reviewService.deleteReview(reviewId, user.getMemberId());
        ResponseCode code = deleted ? ResponseCode.OK : ResponseCode.BAD_REQUEST;
        return new ApiResponse<>(code, deleted);
    }

    // 주문 상세별 리뷰 조회(수정/삭제할 때 불러오는 용도)
    @GetMapping("/{reviewId}")
    public ApiResponse<ProductReviewResponseDTO> getReviewByOrderDetail(@PathVariable Long reviewId) {
        ProductReviewResponseDTO review = reviewService.getReviewByReviewId(reviewId);
        if (review == null) {
            return new ApiResponse<>(ResponseCode.NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.OK, review);
    }

    // 상품별 리뷰 조회
    @GetMapping
    public ApiResponse<List<ProductReviewResponseDTO>> getReviews(@PathVariable Long productId, @AuthenticationPrincipal SecurityUser user) {
        Long memberId = user.getMemberId();
        List<ProductReviewResponseDTO> reviews = reviewService.getReviewsByProduct(productId, memberId);
        return new ApiResponse<>(ResponseCode.OK, reviews);
    }
}