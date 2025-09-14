package com.create.chacha.domains.buyer.areas.mypage.dto.request;

import lombok.Data;

@Data
public class CancelRefundRequestDTO {
    private Integer amount;
    private String content; // 사유
}