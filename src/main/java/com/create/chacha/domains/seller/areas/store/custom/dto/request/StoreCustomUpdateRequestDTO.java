package com.create.chacha.domains.seller.areas.store.custom.dto.request;

import lombok.Data;

@Data
public class StoreCustomUpdateRequestDTO {
    // 선택: 바꾸려면 값 전달, 안 바꾸면 null로 생략
    private Long fontId;   // 글꼴 ID (store_font.id)
    private Long iconId;   // 아이콘 ID (store_icon.id)

    private String fontColor;          // "#000000"
    private String headerFooterColor;  // "#676F58"
    private String noticeColor;        // "#FFF7DB"
    private String descriptionColor;   // "#FFF6EE"
    private String popularColor;       // "#FFF7DB"
}