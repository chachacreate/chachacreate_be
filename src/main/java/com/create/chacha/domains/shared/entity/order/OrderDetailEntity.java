package com.create.chacha.domains.shared.entity.order;

import com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 주문 상세 엔티티
 * <p>
 * 하나의 주문에 담긴 상품 정보를 관리하는 엔티티입니다.
 * 주문 수량, 가격, 상태 등의 정보를 포함합니다.
 * </p>
 *
 * <ul>
 *     <li>{@link #id} - 주문 상세 ID (AUTO_INCREMENT)</li>
 *     <li>{@link #orderInfo} - 연결된 주문 정보</li>
 *     <li>{@link #product} - 주문한 상품</li>
 *     <li>{@link #quantity} - 주문한 상품 수량</li>
 *     <li>{@link #price} - 주문 당시 상품 가격</li>
 *     <li>{@link #status} - 주문 상태</li>
 * </ul>
 */
@Entity
@Table(name = "order_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@EntityListeners(value = AuditingEntityListener.class) // 변경이 일어나면 자동으로 넣어줌
public class OrderDetailEntity {

    /** 주문 상세 ID (AUTO_INCREMENT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 연결된 주문 정보 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_info_id", nullable = false, columnDefinition = "CHAR(36)")
    private OrderInfoEntity orderInfo;

    /** 주문한 상품 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    /** 주문한 상품 수량 */
    private Integer quantity;

    /** 주문 당시 상품 가격 */
    private Integer price;

    /** 주문 상태 (기본, 취소 요청/완료, 환불 요청/완료 등) */
    @Enumerated(EnumType.STRING)
    private OrderAndReservationStatusEnum status;
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