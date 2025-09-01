package com.create.chacha.domains.seller.areas.products.productcrud.dto.response;

import java.util.List;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDetailDTO {
 private Long id;

 private String name;
 private Long price;
 private String detail;
 private Integer stock;

 private Long upCategoryId;
 private String upCategoryName;
 private Long downCategoryId;
 private String downCategoryName;

 private List<String> thumbnails;   // 최대 3장, imageSequence ASC
 private List<String> descriptions; // 0..N장, imageSequence ASC
}
