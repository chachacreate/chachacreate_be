package com.create.chacha.domains.shared.entity.member;

import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 회원 주소 정보 엔티티
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "member")
public class MemberAddressEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 회원 정보 (MemberEntity와 ManyToOne 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    /**
     * 우편번호
     */
    @Convert(converter = AESConverter.class)
    @Column(name = "post_num", nullable = false, length = 20)
    private String postNum;

    /**
     * 도로명 주소
     */
    @Convert(converter = AESConverter.class)
    @Column(nullable = false, length = 255)
    private String road;

    /**
     * 상세 주소
     */
    @Convert(converter = AESConverter.class)
    @Column(nullable = false, length = 255)
    private String detail;

    /**
     * 참고 항목 (optional)
     */
    @Convert(converter = AESConverter.class)
    @Column(length = 255)
    private String extra;

    /**
     * 기본 배송지 여부
     * <p>1: 기본 배송지, 0: 기본 배송지 아님</p>
     */
    @Column(name = "default_check", nullable = false)
    private Boolean defaultCheck = false;
}
