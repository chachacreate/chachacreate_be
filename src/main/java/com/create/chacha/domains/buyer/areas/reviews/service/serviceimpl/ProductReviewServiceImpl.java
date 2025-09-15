package com.create.chacha.domains.buyer.areas.reviews.service.serviceimpl;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.common.util.dto.LegacyProductDTO;
import com.create.chacha.domains.buyer.areas.reviews.dto.response.ProductReviewResponseDTO;
import com.create.chacha.domains.buyer.areas.reviews.repository.ProductReviewRepository;
import com.create.chacha.domains.buyer.areas.reviews.service.ProductReviewService;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.entity.product.ProductEntity;
import com.create.chacha.domains.shared.entity.product.ReviewEntity;
import com.create.chacha.domains.shared.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final LegacyAPIUtil legacyAPIUtil;

    @Override
    public ProductReviewResponseDTO createReview(Long productId, BigDecimal rating, String content, Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        LegacyProductDTO product = legacyAPIUtil.getLegacyProductData(productId);
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(product.getProductId().longValue());
        productEntity.setName(product.getProductName());

        ReviewEntity review = new ReviewEntity();
        review.setMember(member);
        review.setProduct(productEntity);
        review.setRating(rating);
        review.setContent(content);

        reviewRepository.save(review);

        ProductReviewResponseDTO response = ProductReviewResponseDTO.builder()
                .id(review.getId())
                .memberId(member.getId())
                .memberName(member.getName())
                .productId(product.getProductId().longValue())
                .productName(product.getProductName())
                .rating(review.getRating())
                .content(review.getContent())
                .build();

        return response;
    }

    @Override
    public List<ProductReviewResponseDTO> getReviewsByProduct(Long productId) {
        // 리뷰 조회 (MySQL)
        List<ReviewEntity> reviews = reviewRepository.findReviewsByProductId(productId);

        // 상품 정보 조회 (Oracle)
        LegacyProductDTO product = legacyAPIUtil.getLegacyProductData(productId);

        // DTO 변환
        return reviews.stream()
                .map(r -> ProductReviewResponseDTO.builder()
                        .id(r.getId())
                        .memberId(r.getMember().getId())
                        .memberName(r.getMember().getName())
                        .productId(product.getProductId().longValue())
                        .productName(product.getProductName())
                        .rating(r.getRating())
                        .content(r.getContent())
                        .reviewDate(r.getCreatedAt())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
