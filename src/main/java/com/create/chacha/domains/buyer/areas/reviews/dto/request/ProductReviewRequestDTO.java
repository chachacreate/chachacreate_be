package com.create.chacha.domains.buyer.areas.reviews.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductReviewRequestDTO {
    private Long productId; // URL에서 받아올 상품 ID
    private BigDecimal rating;
    private String content;
}
