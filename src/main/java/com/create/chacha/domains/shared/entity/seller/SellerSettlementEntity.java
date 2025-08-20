package com.create.chacha.domains.shared.entity.seller;

import com.create.chacha.config.app.constants.SellerSettlementEnumConverter;
import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.constants.SellerSettlementEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

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
public class SellerSettlementEntity extends BaseEntity {

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
    @Convert(converter = AESConverter.class)
    @Column(nullable = false)
    private Integer amount;

    /**
     * 정산 상태
     * <ul>
     *     <li>0 = 미정산</li>
     *     <li>1 = 정산 완료</li>
     * </ul>
     */
    @Column(nullable = false)
    @Convert(converter = SellerSettlementEnumConverter.class)
    private SellerSettlementEnum status;
}
