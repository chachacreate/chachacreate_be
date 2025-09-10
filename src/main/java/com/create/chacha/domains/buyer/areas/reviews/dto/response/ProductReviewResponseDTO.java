package com.create.chacha.domains.buyer.areas.reviews.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class ProductReviewResponseDTO {
    private Long id;
    private Long memberId;
    private String memberName;
    private Long productId;
    private String productName;
    private BigDecimal rating;
    private String content;
}
