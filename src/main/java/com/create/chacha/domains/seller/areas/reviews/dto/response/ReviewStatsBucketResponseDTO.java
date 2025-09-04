package com.create.chacha.domains.seller.areas.reviews.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class ReviewStatsBucketResponseDTO {
    private double rating;     // 0.0, 0.5, ..., 5.0
    private Long count;        // 해당 버킷 리뷰 수
    private double percentage; // 전체 대비 % (소수 1자리)
}
