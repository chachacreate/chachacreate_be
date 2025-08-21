package com.create.chacha.domains.shared.entity.member;

import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
@ToString
@EntityListeners(value = AuditingEntityListener.class) // 변경이 일어나면 자동으로 넣어줌
public class CartEntity {

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
    private Integer quantity;

    /*
     * 생성 시간
     */
    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdAt;
    /*
     * 수정 시간
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}