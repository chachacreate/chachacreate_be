package com.create.chacha.domains.seller.areas.reviews.service.serviceimpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.common.util.dto.LegacyProductDTO;
import com.create.chacha.common.util.dto.LegacyStoreDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewStatsBucketResponseDTO;
import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewStatsResponseDTO;
import com.create.chacha.domains.seller.areas.reviews.repository.ReviewStatsRepository;
import com.create.chacha.domains.seller.areas.reviews.repository.ReviewStatsRepository.StatsRow;
import com.create.chacha.domains.seller.areas.reviews.service.SellerReviewStatsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerReviewStatsServiceImpl implements SellerReviewStatsService {

    private final ReviewStatsRepository repo;
    private final LegacyAPIUtil legacyAPIUtil;
    private static final List<Double> BUCKETS = buildBuckets();

    private static List<Double> buildBuckets() {
        List<Double> list = new ArrayList<>();
        for (int i = 0; i <= 10; i++) list.add(i * 0.5);
        return Collections.unmodifiableList(list);
    }

    // 특정 store 통계 조회
    @Override
    @Transactional(readOnly = true)
    public ReviewStatsResponseDTO getStoreStats(String storeUrl) {
        LegacyStoreDTO store = legacyAPIUtil.getLegacyStoreData(storeUrl);
        Integer storeId = store.getStoreId();

        List<StatsRow> allReviews = repo.findStatsOnly(); // 전체 리뷰 조회

        // storeId로 필터링
        List<StatsRow> filtered = allReviews.stream()
                .filter(r -> storeId.equals(getProductStoreId(r.getProductId())))
                .collect(Collectors.toList());

        return toResponse(filtered);
    }

    // 특정 product 통계 조회
    @Override
    @Transactional(readOnly = true)
    public ReviewStatsResponseDTO getProductStats(String storeUrl, Long productId) {
        LegacyStoreDTO store = legacyAPIUtil.getLegacyStoreData(storeUrl);
        Integer storeId = store.getStoreId();

        LegacyProductDTO product = legacyAPIUtil.getLegacyProductData(productId);

        // product가 존재하고 storeId가 일치할 때만 통계 계산
        if (product == null || !storeId.equals(product.getStoreId())) {
            return new ReviewStatsResponseDTO(); // 빈 통계 반환
        }

        List<StatsRow> allReviews = repo.findStatsOnly(); // 전체 리뷰 조회

        // productId 필터링
        List<StatsRow> filtered = allReviews.stream()
                .filter(r -> productId.equals(r.getProductId()))
                .collect(Collectors.toList());

        return toResponse(filtered);
    }

    /** productId로 LegacyProductDTO 조회 후 storeId 반환 */
    private Integer getProductStoreId(Long productId) {
        LegacyProductDTO product = legacyAPIUtil.getLegacyProductData(productId);
        return (product != null) ? product.getStoreId() : null;
    }

    /** 필터링된 리뷰 리스트 → DTO 변환 */
    private ReviewStatsResponseDTO toResponse(List<StatsRow> rows) {
        Map<Double, Long> countMap = rows.stream()
                .collect(Collectors.toMap(StatsRow::getBucket, StatsRow::getCnt, Long::sum));

        long total = countMap.values().stream().mapToLong(Long::longValue).sum();

        List<ReviewStatsBucketResponseDTO> buckets = new ArrayList<>();
        for (double r : BUCKETS) {
            long c = countMap.getOrDefault(r, 0L);
            double pct = (total == 0) ? 0.0 : round1((c * 100.0) / total);
            buckets.add(ReviewStatsBucketResponseDTO.builder()
                    .rating(r)
                    .count(c)
                    .percentage(pct)
                    .build());
        }

        return ReviewStatsResponseDTO.builder()
                .totalReviews(total)
                .buckets(buckets)
                .build();
    }

    private double round1(double v) {
        return BigDecimal.valueOf(v).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
