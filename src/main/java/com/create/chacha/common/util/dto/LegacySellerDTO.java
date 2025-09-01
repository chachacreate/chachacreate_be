package com.create.chacha.common.util.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LegacySellerDTO {
    /**
     * 판매자 고유 ID (기본 키)
     */
    private Integer sellerId;

    /**
     * 회원 ID (외래키, 회원 테이블과 연관)
     */
    private Integer memberId;

    /**
     * 판매자 등록 또는 개설 날짜
     */
    private Date openingDate;

    /**
     * 판매자 계좌 번호
     */
    private String account;

    /**
     * 판매자 계좌 은행명
     */
    private String accountBank;

    /**
     * 판매자 프로필 정보 및 소개
     */
    private String profileInfo;

    /**
     * 개인판매자 여부
     */
    private Integer personalCheck;
}
