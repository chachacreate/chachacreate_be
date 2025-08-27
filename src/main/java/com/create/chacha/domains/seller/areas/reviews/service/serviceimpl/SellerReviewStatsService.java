package com.create.chacha.domains.seller.areas.reviews.service.serviceimpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewStatsBucketResponseDTO;
import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewStatsResponseDTO;
import com.create.chacha.domains.seller.areas.reviews.repository.ReviewStatsRepository;
import com.create.chacha.domains.seller.areas.reviews.repository.ReviewStatsRepository.StatsRow;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerReviewStatsService implements SellerReviewStatsServiceImpl {

    private final ReviewStatsRepository repo;
    private static final List<Double> BUCKETS = buildBuckets();

    private static List<Double> buildBuckets() {
        List<Double> list = new ArrayList<>();
        for (int i = 0; i <= 10; i++) list.add(i * 0.5);
        return Collections.unmodifiableList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewStatsResponseDTO getStoreStats(String storeUrl) {
        List<StatsRow> rows = repo.findStatsByStoreUrl(storeUrl);
        return toResponse(rows);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewStatsResponseDTO getProductStats(String storeUrl, Long productId) {
        List<StatsRow> rows = repo.findStatsByStoreUrlAndProductId(storeUrl, productId);
        return toResponse(rows);
    }

    private ReviewStatsResponseDTO toResponse(List<StatsRow> rows) {
        Map<Double, Long> countMap = rows.stream()
                .collect(Collectors.toMap(StatsRow::getBucket, StatsRow::getCnt));

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
