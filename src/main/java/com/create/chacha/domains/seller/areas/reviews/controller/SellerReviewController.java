package com.create.chacha.domains.seller.areas.reviews.controller;

import java.util.List;

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

    private final SellerReviewQueryServiceImpl reviewService;
    private final SellerReviewStatsServiceImpl statsService;

    /** 스토어 통계 - 수정 ok */
    @GetMapping("/reviews/stats")
    public ApiResponse<ReviewStatsResponseDTO> getStoreStats(@PathVariable("storeUrl") String storeUrl) {
        ReviewStatsResponseDTO body = statsService.getStoreStats(storeUrl);

        if (body == null || isEmptyStats(body)) {
            return new ApiResponse<>(ResponseCode.SELLER_REVIEW_STATS_NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.SELLER_REVIEW_STATS_FOUND, body);
    }

    /** 상품 통계 - 수정 ok */
    @GetMapping("/reviews/stats/{productId}")
    public ApiResponse<ReviewStatsResponseDTO> getProductStats(@PathVariable("storeUrl") String storeUrl,
                                                               @PathVariable("productId") Long productId) {
        ReviewStatsResponseDTO body = statsService.getProductStats(storeUrl, productId);

        if (body == null || isEmptyStats(body)) {
            return new ApiResponse<>(ResponseCode.SELLER_PRODUCT_REVIEW_STATS_NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.SELLER_PRODUCT_REVIEW_STATS_FOUND, body);
    }

    /** 스토어 리뷰 목록 - 수정 ok */
    @GetMapping("/review")
    public ApiResponse<List<ReviewListItemDTO>> getReviews(@PathVariable("storeUrl") String storeUrl) {
        List<ReviewListItemDTO> body = reviewService.getReviewsByStore(storeUrl);

        if (body == null || body.isEmpty()) {
            return new ApiResponse<>(ResponseCode.SELLER_REVIEWS_NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.SELLER_REVIEWS_FOUND, body);
    }

    /** 상품 리뷰 목록 - 수정 ok */
    @GetMapping("/review/{productId}")
    public ApiResponse<List<ReviewListItemDTO>> getReviewsByProduct(@PathVariable("storeUrl") String storeUrl,
                                                                    @PathVariable("productId") Long productId) {
        List<ReviewListItemDTO> body = reviewService.getReviewsByStoreAndProduct(storeUrl, productId);

        if (body == null || body.isEmpty()) {
            return new ApiResponse<>(ResponseCode.SELLER_PRODUCT_REVIEWS_NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.SELLER_PRODUCT_REVIEWS_FOUND, body);
    }

    /** 통계가 '사실상 비어있는지' 판단: total=0 이거나 buckets 전부 0개수 */
    private boolean isEmptyStats(ReviewStatsResponseDTO dto) {
        if (dto.getTotalReviews() == null || dto.getTotalReviews() == 0L) return true;
        if (dto.getBuckets() == null || dto.getBuckets().isEmpty()) return true;
        // 혹시 total>0인데 buckets가 모두 count=0인 비정상 데이터도 방어
        return dto.getBuckets().stream().allMatch(b -> b.getCount() == null || b.getCount() == 0L);
    }
}