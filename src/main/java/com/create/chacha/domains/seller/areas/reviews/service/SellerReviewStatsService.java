package com.create.chacha.domains.seller.areas.reviews.service;

import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewStatsResponseDTO;

public interface SellerReviewStatsService {
    ReviewStatsResponseDTO getStoreStats(String storeUrl);
    ReviewStatsResponseDTO getProductStats(String storeUrl, Long productId);
}
