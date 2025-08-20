package com.create.chacha.domains.shared.entity.classcore;

import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 클래스 예약 엔티티
 * <p>
 * 각 클래스별 예약 정보를 관리.
 * 예약 상태, 예약 번호, 결제 키, 예약 시간 등을 포함하며,
 * BaseEntity를 상속받아 생성/수정 시간과 삭제 여부를 자동 관리.
 */
@Entity
@Table(name = "class_reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"classInfo", "member"})
public class ClassReservationEntity extends BaseEntity {

    /**
     * 예약 기본 키 (UUID)
     */
    @Id
    @Column(length = 36, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 예약된 클래스
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_info_id", nullable = false)
    private ClassInfoEntity classInfo;

    /**
     * 예약한 회원
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    /**
     * 예약 상태
     */
    @Column(nullable = false, length = 50)
    private OrderAndReservationStatusEnum status;

    /**
     * 사용자에게 보여지는 예약 번호
     */
    @Column(name = "reservation_number", nullable = false, length = 50, unique = true)
    private String reservationNumber;

    /**
     * 토스 페이먼츠 결제 키
     */
    @Convert(converter = AESConverter.class)
    @Column(name = "payment_key", nullable = false, length = 100)
    private String paymentKey;

    /**
     * 예약한 클래스 시간
     */
    @Column(name = "reserved_time", nullable = false)
    private LocalDateTime reservedTime;

    /** 예약 번호 생성 */
    @PrePersist
    public void prePersist() {
        if (this.getCreatedAt() == null) {
            this.setCreatedAt(LocalDateTime.now());
        }
        if (this.reservationNumber == null) {
            // 날짜 기반 prefix
            String datePart = this.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // 난수 or 시퀀스 (예시: 6자리 랜덤)
            String randomPart = String.format("%06d", new Random().nextInt(1_000_000));

            // 조합 (ex: RV-20250820-123456)
            String rawOrderNumber = "RV-" + datePart + "-" + randomPart;

            this.reservationNumber = rawOrderNumber;
        }
    }
}
