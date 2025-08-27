package com.create.chacha.domains.seller.areas.reviews.service.serviceimpl;

import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewStatsResponseDTO;

public interface SellerReviewStatsServiceImpl {
    ReviewStatsResponseDTO getStoreStats(String storeUrl);
    ReviewStatsResponseDTO getProductStats(String storeUrl, Long productId);
}
