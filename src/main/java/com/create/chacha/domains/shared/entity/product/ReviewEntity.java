package com.create.chacha.domains.shared.entity.product;

import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 리뷰 엔티티
 * <p>
 * 상품에 대해 작성된 리뷰 정보를 관리하는 엔티티입니다.
 * 회원과 상품과 연관되며, 평점과 리뷰 내용을 포함합니다.
 * </p>
 *
 * <ul>
 *     <li>{@link #id} - 리뷰 ID (AUTO_INCREMENT)</li>
 *     <li>{@link #member} - 리뷰 작성 회원</li>
 *     <li>{@link #product} - 리뷰 대상 상품</li>
 *     <li>{@link #rating} - 평점 (1.0 ~ 5.0, 0.5 단위)</li>
 *     <li>{@link #content} - 리뷰 내용</li>
 * </ul>
 */
@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity extends BaseEntity {

    /** 리뷰 ID (AUTO_INCREMENT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 리뷰 작성 회원 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    /** 리뷰 대상 상품 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    /** 평점 (1.0 ~ 5.0, 0.5 단위) */
    private BigDecimal rating;

    /** 리뷰 내용 */
    private String content;
}
