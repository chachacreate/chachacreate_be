package com.create.chacha.domains.seller.areas.reviews.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewStatsResponseDTO {
    private Long totalReviews;                                 // 전체 리뷰 수
    private List<ReviewStatsBucketResponseDTO> buckets;        // 0.0 -> 5.0 오름차순 11개
}