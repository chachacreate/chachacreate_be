package com.create.chacha.domains.seller.areas.review.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.create.chacha.domains.seller.areas.review.dto.response.ReviewListItemDTO;
import com.create.chacha.domains.seller.areas.review.repository.ReviewReadRepository;
import com.create.chacha.domains.seller.areas.review.service.serviceimpl.SellerReviewQueryServiceImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerReviewQueryService implements SellerReviewQueryServiceImpl {

    private final ReviewReadRepository repo;

    @Override
    public List<ReviewListItemDTO> getReviewsByStore(String storeUrl) {
        return repo.findReviewsByStoreUrl(storeUrl).stream()
                .map(r -> ReviewListItemDTO.builder()
                        .reviewId(r.getReviewId())
                        .reviewCreatedAt(r.getReviewCreatedAt())
                        .productThumbnailUrl(r.getProductThumbnailUrl())
                        .productName(r.getProductName())
                        .authorId(r.getAuthorId())
                        .authorName(r.getAuthorName())
                        .content(r.getContent())
                        .productCreatedAt(r.getProductCreatedAt())
                        .productRating(r.getProductRating())   // 이미 "X.X/5.0"
                        .likeCount(r.getLikeCount())
                        .reviewUpdatedAt(r.getReviewUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
