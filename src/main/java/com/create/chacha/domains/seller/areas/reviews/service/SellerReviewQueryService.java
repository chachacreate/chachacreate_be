package com.create.chacha.domains.seller.areas.reviews.service;

import java.util.List;

import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewListItemDTO;

public interface SellerReviewQueryService {
	List<ReviewListItemDTO> getReviewsByStore(String storeUrl);
	List<ReviewListItemDTO> getReviewsByStoreAndProduct(String storeUrl, Long productId);
}
