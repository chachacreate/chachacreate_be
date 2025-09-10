package com.create.chacha.common.util.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class LegacyProductDTO {
    private Integer productId;
    private Integer storeId;
    private String typeCategoryId;
    private String dcategoryId;
    private String productName;

    private Integer price;

    /**
     * 상품 상세 설명 (DB에서는 CLOB 타입으로 저장)
     */
    private String productDetail;

    private Integer stock;
    private Instant productDate;
    private Instant lastModifiedDate;
    private Integer saleCnt;
    private Integer viewCnt;
    private Integer flagshipCheck;
    private Integer deleteCheck;
}
