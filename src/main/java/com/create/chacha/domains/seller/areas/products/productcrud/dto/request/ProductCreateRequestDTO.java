package com.create.chacha.domains.seller.areas.products.productcrud.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductCreateRequestDTO {
    private ProductCorePayload product;
    private MultipartFile[] thumbnails;   // 3장
    private MultipartFile[] descriptions; // 0..N

    @Data
    public static class ProductCorePayload {
        private Long sellerId;
        private Long upCategoryId;
        private Long downCategoryId;
        private String name;
        private Integer price;
        private String detail;    // nullable
        private Integer stock;    // >= 0
    }
}

