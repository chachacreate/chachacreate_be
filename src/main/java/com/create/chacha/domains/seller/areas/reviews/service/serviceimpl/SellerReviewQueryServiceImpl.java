package com.create.chacha.domains.seller.areas.reviews.service.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.common.util.dto.LegacyProductDTO;
import com.create.chacha.common.util.dto.LegacyStoreDTO;
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

    private final ReviewReadRepository reviewReadRepository;
    private final LegacyAPIUtil legacyAPIUtil;


    // 스토어의 전체 리뷰 조회
    @Override
    @Transactional(readOnly = true)
    public List<ReviewListItemDTO> getReviewsByStore(String storeUrl) {
        LegacyStoreDTO store = legacyAPIUtil.getLegacyStoreData(storeUrl);
        Integer storeId = store.getStoreId();

        List<ReviewRow> rows = reviewReadRepository.findReviewsOnly(); // 전체 리뷰 조회

        List<ReviewListItemDTO> dtos = new ArrayList<>();
        for (ReviewRow row : rows) {
            LegacyProductDTO product = legacyAPIUtil.getLegacyProductData(row.getProductId());
            if (product != null && storeId.equals(product.getStoreId())) { // storeId로 조건 확인
                dtos.add(toDTO(row, product));
            }
        }
        return dtos;
    }

    // 스토어의 상품별 리뷰 조회
    @Override
    @Transactional(readOnly = true)
    public List<ReviewListItemDTO> getReviewsByStoreAndProduct(String storeUrl, Long productId) {
        LegacyStoreDTO store = legacyAPIUtil.getLegacyStoreData(storeUrl);
        Integer storeId = store.getStoreId();

        List<ReviewRow> rows = reviewReadRepository.findReviewsOnly(); // 전체 리뷰 조회
        List<ReviewListItemDTO> dtos = new ArrayList<>();

        for (ReviewRow row : rows) {
            LegacyProductDTO product = legacyAPIUtil.getLegacyProductData(row.getProductId());
            if (product != null
                    && storeId.equals(product.getStoreId())
                    && productId.equals(product.getProductId().longValue())) { // storeId & productId로 조건 확인
                dtos.add(toDTO(row, product));
            }
        }
        return dtos;
    }

    private ReviewListItemDTO toDTO(ReviewRow row, LegacyProductDTO product) {
        String name = reviewReadRepository.findAuthorNameByReviewId(row.getReviewId());

        ReviewListItemDTO dto = new ReviewListItemDTO();
        dto.setReviewId(row.getReviewId());
        dto.setReviewCreatedAt(row.getReviewCreatedAt());
        dto.setReviewUpdatedAt(row.getReviewUpdatedAt());
        dto.setAuthorId(row.getAuthorId());
        dto.setAuthorName(name); // 리뷰자 이름 복호화
        dto.setProductId(row.getProductId());
        dto.setContent(row.getContent());
        dto.setLikeCount(row.getLikeCount());
        dto.setProductRating(row.getProductRating());

        if (product != null) {
            dto.setProductName(product.getProductName());
            dto.setProductThumbnailUrl(product.getThumbnailUrl());
//            dto.setProductCreatedAt(product.getProductDate());   // 상품 등록순으로 정렬 시 필요
        }

        return dto;
    }
}
