package com.create.chacha.domains.shared.entity.order;

import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 주문 환불 엔티티
 * <p>
 * 주문 상세에 대한 환불 요청 정보를 관리하는 엔티티입니다.
 * 환불 사유와 환불 금액, 요청 시간 등을 포함합니다.
 * </p>
 *
 * <ul>
 *     <li>{@link #id} - 환불 ID (AUTO_INCREMENT)</li>
 *     <li>{@link #orderDetail} - 환불 대상 주문 상세</li>
 *     <li>{@link #content} - 환불 사유</li>
 *     <li>{@link #amount} - 환불 금액</li>
 * </ul>
 */
@Entity
@Table(name = "order_refund")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRefundEntity extends BaseEntity {

    /** 환불 ID (AUTO_INCREMENT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 환불 대상 주문 상세 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", nullable = false)
    private OrderDetailEntity orderDetail;

    /** 환불 사유 */
    @Column(nullable = false, length = 255)
    private String content;

    /** 환불 금액 */
    @Column(nullable = false)
    private Integer amount;
}

