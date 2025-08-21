package com.create.chacha.domains.shared.entity.order;

import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.constants.OrderInfoStatusEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.member.MemberAddressEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(value = AuditingEntityListener.class) // 변경이 일어나면 자동으로 넣어줌
public class OrderInfoEntity {

    /** 주문 ID (UUID) */
    @Id
    @Column(columnDefinition = "CHAR(36)")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** 배송지 정보 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_address_id", nullable = false)
    private MemberAddressEntity memberAddress;

    /** 날짜+주문번호+순서를 조합한 유효 ID */
    @Convert(converter = AESConverter.class)
    private String orderNumber;

    /** 수령인 이름 */
    @Convert(converter = AESConverter.class)
    private String name;

    /** 수령인 전화번호 */
    @Convert(converter = AESConverter.class)
    private String phone;

    /** 주문 상태 (주문 완료, 발송 완료, 배송 완료, 취소 요청, 취소 완료, 환불 요청, 환불 완료 등) */
    @Enumerated(EnumType.STRING)
    private OrderInfoStatusEnum status;

    /** 주문에 포함된 총 금액 */
    private Integer totalAmount;

    /** 결제 키 (토스 페이먼츠 API) */
    @Convert(converter = AESConverter.class)
    private String paymentKey;

    /** 주문 상세 리스트 */
    @OneToMany(mappedBy = "orderInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetailEntity> orderDetails;

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

