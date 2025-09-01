package com.create.chacha.domains.seller.areas.products.productcrud.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductUpdateDTO {
    private Long upCategoryId;   // null이면 미수정
    private Long downCategoryId; // null이면 미수정
    private String name;
    private Integer price;
    private String detail;
    private Integer stock;
}