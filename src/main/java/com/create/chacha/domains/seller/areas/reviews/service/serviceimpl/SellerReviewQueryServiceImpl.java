package com.create.chacha.domains.seller.areas.reviews.service.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewListItemDTO;
import com.create.chacha.domains.seller.areas.reviews.repository.ReviewReadRepository;
import com.create.chacha.domains.seller.areas.reviews.repository.ReviewReadRepository.ReviewRow;
import com.create.chacha.domains.seller.areas.reviews.service.SellerReviewQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerReviewQueryServiceImpl implements SellerReviewQueryService {

    private final ReviewReadRepository repo;
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewListItemDTO> getReviewsByStore(String storeUrl) {
        List<ReviewRow> rows = repo.findReviewsByStoreUrl(storeUrl);
        return rows.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewListItemDTO> getReviewsByStoreAndProduct(String storeUrl, Long productId) {
        List<ReviewRow> rows = repo.findReviewsByStoreUrlAndProductId(storeUrl, productId);
        return rows.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ReviewListItemDTO toDTO(ReviewRow r) {
        return ReviewListItemDTO.builder()
                .reviewId(r.getReviewId())
                .reviewCreatedAt(r.getReviewCreatedAt())
                .reviewUpdatedAt(r.getReviewUpdatedAt())
                .authorId(r.getAuthorId())
                .authorName(r.getAuthorName())
                .content(r.getContent())
                .productName(r.getProductName())
                .productCreatedAt(r.getProductCreatedAt())
                .productThumbnailUrl(r.getProductThumbnailUrl())
                .likeCount(r.getLikeCount())
                .productRating(r.getProductRating())
                .build();
    }
}
