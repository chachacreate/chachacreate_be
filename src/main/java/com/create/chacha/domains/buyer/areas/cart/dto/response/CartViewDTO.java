package com.create.chacha.domains.buyer.areas.cart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartViewDTO {
    private Long cartId;
    private Long memberId;
    private Long productId;
    private Integer productCnt;
    private String productName;
    private String productDetail;
    private Integer price;
    private Integer stock;
    private Long storeId;
    private String storeName;
    private String storeUrl;
    private String pImgUrl;
}