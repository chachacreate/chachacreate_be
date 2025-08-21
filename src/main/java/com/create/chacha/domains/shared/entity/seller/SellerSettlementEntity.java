package com.create.chacha.domains.shared.entity.seller;

import com.create.chacha.config.app.constants.SellerSettlementEnumConverter;
import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.constants.SellerSettlementEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 판매자 정산 엔티티
 * <p>
 * 매월 1일 기준으로 판매자별 정산 금액을 기록.
 * 정산 상태(미정산/정산 완료), 금액 등을 관리.
 * BaseEntity를 상속받아 생성/수정 시간을 자동 관리.
 */
@Entity
@Table(name = "seller_settlement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"seller"})
@EntityListeners(value = AuditingEntityListener.class) // 변경이 일어나면 자동으로 넣어줌
public class SellerSettlementEntity{

    /**
     * 정산 ID (자동 증가)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 정산 대상 판매자 (회원)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private MemberEntity seller;

    /**
     * 월별 정산 금액
     */
    private Integer amount;

    /**
     * 정산 상태
     * <ul>
     *     <li>0 = 미정산</li>
     *     <li>1 = 정산 완료</li>
     * </ul>
     */
    @Convert(converter = SellerSettlementEnumConverter.class)
    @Column(columnDefinition = "TINYINT")
    private SellerSettlementEnum status;
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
