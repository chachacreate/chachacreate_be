package com.create.chacha.domains.seller.areas.review.service.serviceimpl;

import java.util.List;

import com.create.chacha.domains.seller.areas.review.dto.response.ReviewListItemDTO;

public interface SellerReviewQueryServiceImpl {
	List<ReviewListItemDTO> getReviewsByStore(String storeUrl);
	List<ReviewListItemDTO> getReviewsByStoreAndProduct(String storeUrl, Long productId);
}
