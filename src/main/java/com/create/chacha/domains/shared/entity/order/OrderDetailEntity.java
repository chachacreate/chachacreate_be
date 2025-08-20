package com.create.chacha.domains.shared.entity.order;

import com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

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
public class OrderDetailEntity extends BaseEntity {

    /** 주문 상세 ID (AUTO_INCREMENT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 연결된 주문 정보 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_info_id", nullable = false)
    private OrderInfoEntity orderInfo;

    /** 주문한 상품 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    /** 주문한 상품 수량 */
    @Column(nullable = false)
    private Integer quantity;

    /** 주문 당시 상품 가격 */
    @Column(nullable = false)
    private Integer price;

    /** 주문 상태 (기본, 취소 요청/완료, 환불 요청/완료 등) */
    @Column(nullable = false, length = 50)
    private OrderAndReservationStatusEnum status;
}