package com.create.chacha.domains.buyer.areas.mainhome.main.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeDTO {
    private Long storeId;
    private String storeName;
    private String storeUrl;
    private String logoImg;
    private String storeDetail;
    private String categoryName;  // down_category_name으로 사용
    private Integer saleCnt;
    private Integer rnk;
}