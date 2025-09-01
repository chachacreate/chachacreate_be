package com.create.chacha.domains.buyer.areas.mainhome.main.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeProductDTO {
    private Long productId;
    private Long downCategoryId;
    private String name;
    private Integer price;
    private String detail;
    private Integer stock;
    private LocalDateTime productDate;
    private LocalDateTime lastModifiedDate;
    private Integer saleCnt;
    private Integer viewCnt;
    private String logoImg;
    private String storeDetail;
    private String storeName;

    // 조인 필드
    private String downCategoryName;
    private String upCategoryName;
    private String pImgUrl;
    private String storeUrl;
}
