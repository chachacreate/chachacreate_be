package com.create.chacha.domains.shared.entity.member;

import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.constants.MemberRoleEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 회원 정보를 저장하는 엔티티 클래스.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "member")
public class MemberEntity extends BaseEntity {
    /**
     * 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회원 로그인 ID (이메일)
     * <p>중복 불가</p>
     */
    private String email;

    /**
     * 회원 이름
     * <p>한글만 허용, 최대 5글자</p>
     */
    @Convert(converter = AESConverter.class)
    private String name;

    /**
     * 비밀번호
     * <p>8자 이상, 특수문자/영문/숫자 포함</p>
     */
    private String password;

    /**
     * 전화번호
     * <p>정규표현식 검증 필요</p>
     */
    @Convert(converter = AESConverter.class)
    private String phone;

    /**
     * 주민등록번호 (뒷자리 한 글자까지 저장)
     */
    @Convert(converter = AESConverter.class)
    private String registrationNumber;  // 주민등록번호

    /**
     * 회원 권한
     * <p>USER, SELLER, PERSONAL_SELLER, ADMIN</p>
     */
    @Enumerated(EnumType.STRING)
    private MemberRoleEnum memberRole;
}
