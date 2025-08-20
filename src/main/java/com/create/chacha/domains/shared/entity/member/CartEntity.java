package com.create.chacha.domains.shared.entity.member;

import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 장바구니 엔티티
 * <p>
 * 회원이 장바구니에 담은 상품 정보를 관리하는 엔티티입니다.
 * 상품 ID, 수량, 생성/수정 시간을 포함합니다.
 * </p>
 *
 * <ul>
 *     <li>{@link #id} - 장바구니 ID (AUTO_INCREMENT)</li>
 *     <li>{@link #member} - 장바구니 주인 회원</li>
 *     <li>{@link #product} - 장바구니에 담긴 상품</li>
 *     <li>{@link #quantity} - 담긴 상품 수량</li>
 * </ul>
 */
@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartEntity extends BaseEntity {

    /** 장바구니 ID (AUTO_INCREMENT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 장바구니 주인 회원 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    /** 장바구니에 담긴 상품 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    /** 담긴 상품 수량 */
    @Column(nullable = false)
    private Integer quantity;
}

