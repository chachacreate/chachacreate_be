package com.create.chacha.domains.buyer.areas.reviews.service;

import com.create.chacha.domains.buyer.areas.reviews.dto.response.ProductReviewResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductReviewService {
    ProductReviewResponseDTO createReview(Long productId, BigDecimal rating, String content, Long memberId);
    ProductReviewResponseDTO updateReview(Long productId, Long reviewId, BigDecimal rating, String content, Long memberId);
    boolean deleteReview(Long reviewId, Long memberId);

//    List<ProductReviewResponseDTO> getReviewsByProduct(Long productId);
    List<ProductReviewResponseDTO> getReviewsByProduct(Long productId, Long memberId);

    ProductReviewResponseDTO getReviewByReviewId(Long reviewId);
}
