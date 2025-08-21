package com.create.chacha.domains.shared.entity.seller;

import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 판매자 정보 엔티티
 * <p>
 */
@Entity
@Table(name = "seller")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class SellerEntity extends BaseEntity {

    /**
     * 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 판매자 계정 (회원과 1:1 관계)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private MemberEntity member;

    /**
     * 판매 수익 정산 계좌 번호
     */
    @Convert(converter = AESConverter.class)
    private String account;

    /**
     * 판매 수익 정산 계좌 은행
     */
    @Convert(converter = AESConverter.class)
    private String bank;
}
