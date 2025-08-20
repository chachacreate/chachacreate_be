package com.create.chacha.domains.shared.entity.order;

import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.constants.OrderInfoStatusEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.member.MemberAddressEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * 주문 정보 엔티티
 * <p>
 * 회원의 주문 정보를 관리하는 엔티티입니다.
 * 주문 번호, 수령인 정보, 상태, 총 금액, 결제 정보 등을 포함합니다.
 * </p>
 *
 * <ul>
 *     <li>{@link #id} - 주문 ID (UUID)</li>
 *     <li>{@link #memberAddress} - 배송지 정보</li>
 *     <li>{@link #orderNumber} - 날짜+주문번호+순서를 조합한 유효 ID</li>
 *     <li>{@link #name} - 수령인 이름</li>
 *     <li>{@link #phone} - 수령인 전화번호</li>
 *     <li>{@link #status} - 주문 상태</li>
 *     <li>{@link #totalAmount} - 주문에 포함된 총 금액</li>
 *     <li>{@link #paymentKey} - 결제 키 (토스 페이먼츠 API)</li>
 *     <li>{@link #orderDetails} - 주문 상세 리스트</li>
 * </ul>
 */
@Entity
@Table(name = "order_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderInfoEntity extends BaseEntity {

    /** 주문 ID (UUID) */
    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** 배송지 정보 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_address_id", nullable = false)
    private MemberAddressEntity memberAddress;

    /** 날짜+주문번호+순서를 조합한 유효 ID */
    @Convert(converter = AESConverter.class)
    @Column(name = "order_number", nullable = false, length = 50, unique = true)
    private String orderNumber;

    /** 수령인 이름 */
    @Convert(converter = AESConverter.class)
    @Column(nullable = false, length = 50)
    private String name;

    /** 수령인 전화번호 */
    @Convert(converter = AESConverter.class)
    @Column(nullable = false, length = 20)
    private String phone;

    /** 주문 상태 (주문 완료, 발송 완료, 배송 완료, 취소 요청, 취소 완료, 환불 요청, 환불 완료 등) */
    @Column(nullable = false, length = 50)
    private OrderInfoStatusEnum status;

    /** 주문에 포함된 총 금액 */
    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    /** 결제 키 (토스 페이먼츠 API) */
    @Convert(converter = AESConverter.class)
    @Column(name = "payment_key", nullable = false, length = 255)
    private String paymentKey;

    /** 주문 상세 리스트 */
    @OneToMany(mappedBy = "orderInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetailEntity> orderDetails;

    /** 주문 번호 생성 */
    @PrePersist
    public void prePersist() {
        if (getCreatedAt() == null) {
            setCreatedAt(LocalDateTime.now());
        }
        if (this.orderNumber == null) {
            // 날짜 기반 prefix
            String datePart = getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // 난수 or 시퀀스 (예시: 6자리 랜덤)
            String randomPart = String.format("%06d", new Random().nextInt(999999));

            // 조합 (ex: OD-20250820-123456)
            String rawOrderNumber = "OD-" + datePart + "-" + randomPart;

            this.orderNumber = rawOrderNumber;
        }
    }
}

