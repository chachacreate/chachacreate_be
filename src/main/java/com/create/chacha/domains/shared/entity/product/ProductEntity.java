package com.create.chacha.domains.shared.entity.product;

import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.category.DownCategoryEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;


/**
 * 상품 엔티티
 * <p>
 * 판매자가 등록하는 상품을 관리.
 * 재고, 판매량, 조회 수, 대표 상품 여부 등을 포함.
 * Soft Delete 적용.
 */
@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"seller", "downCategory"})
public class ProductEntity extends BaseEntity {

    /**
     * 상품 ID (자동 증가)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 판매자 (MemberEntity와 매핑)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private MemberEntity seller;

    /**
     * 소분류 카테고리 (DownCategoryEntity와 매핑)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "down_category_id", nullable = false)
    private DownCategoryEntity downCategory;

    /**
     * 상품명
     */
    private String name;

    /**
     * 상품 가격
     */
    private Integer price;

    /**
     * 상품 설명
     */
    private String detail;

    /**
     * 상품 재고
     */
    private Integer stock = 0;

    /**
     * 판매된 수량
     */
    private Integer saleCount = 0;

    /**
     * 조회 수
     */
    private Integer viewCount = 0;

    /**
     * 대표 상품 여부 (true: 대표 상품)
     */
    @Column(nullable = false, columnDefinition = "TINYINT")
    private Boolean isFlagship = false;
}

