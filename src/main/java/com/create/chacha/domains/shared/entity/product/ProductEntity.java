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
    @Column(nullable = false)
    private String name;

    /**
     * 상품 가격
     */
    @Column(nullable = false)
    private Integer price;

    /**
     * 상품 설명
     */
    @Column(columnDefinition = "TEXT")
    private String detail;

    /**
     * 상품 재고
     */
    @Column(nullable = false)
    private Integer stock = 0;

    /**
     * 판매된 수량
     */
    @Column(name = "sale_count", nullable = false)
    private Integer saleCount = 0;

    /**
     * 조회 수
     */
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    /**
     * 대표 상품 여부 (true: 대표 상품)
     */
    @Column(name = "is_flagship", nullable = false)
    private Boolean isFlagship = false;

    /**
     * 삭제 시각 (Soft Delete 용)
     */
    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;

    /**
     * 삭제 여부 (false = 사용 중, true = 삭제됨)
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}

