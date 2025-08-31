package com.create.chacha.domains.shared.entity.product;

import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.category.DownCategoryEntity;
import jakarta.persistence.*;
import lombok.*;


/**
 * 상품 엔티티
 * <p>
 * 판매자가 등록하는 상품을 관리.
 * 재고, 판매량, 조회 수, 대표 상품 여부 등을 포함.
 * Soft Delete 적용.
 */
//import 변경
import com.create.chacha.domains.shared.entity.seller.SellerEntity; // ✅ seller 테이블과 매핑되는 엔티티

@Entity
@Table(name = "product")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"seller", "downCategory"})
public class ProductEntity extends BaseEntity {

 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 // ✅ seller_id → seller 테이블과 정확히 매핑
 @ManyToOne(fetch = FetchType.LAZY, optional = false)
 @JoinColumn(name = "seller_id", nullable = false)
 private SellerEntity seller;

 @ManyToOne(fetch = FetchType.LAZY, optional = false)
 @JoinColumn(name = "down_category_id", nullable = false)
 private DownCategoryEntity downCategory;

 @Column(nullable = false)
 private String name;

 @Column(nullable = false)
 private Integer price;

 private String detail;

 @Builder.Default
 @Column(nullable = false)
 private Integer stock = 0;

 @Builder.Default
 @Column(name = "sale_count", nullable = false)
 private Integer saleCount = 0;

 @Builder.Default
 @Column(name = "view_count", nullable = false)
 private Integer viewCount = 0;

 @Builder.Default
 @Column(nullable = false, columnDefinition = "TINYINT")
 private Boolean isFlagship = false;

 @PrePersist
 private void applyDefaults() {
     if (stock == null) stock = 0;
     if (saleCount == null) saleCount = 0;
     if (viewCount == null) viewCount = 0;
     if (isFlagship == null) isFlagship = false;
 }
}

