package com.create.chacha.domains.shared.entity.order;

import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
@ToString(callSuper = true)
@EntityListeners(value = AuditingEntityListener.class) // 변경이 일어나면 자동으로 넣어줌
public class OrderRefundEntity {

    /** 환불 ID (AUTO_INCREMENT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 환불 대상 주문 상세 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", nullable = false)
    private OrderDetailEntity orderDetail;

    /** 환불 사유 */
    private String content;

    /** 환불 금액 */
    private Integer amount;
    /*
     * 생성 시간
     */
    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdAt;
}

