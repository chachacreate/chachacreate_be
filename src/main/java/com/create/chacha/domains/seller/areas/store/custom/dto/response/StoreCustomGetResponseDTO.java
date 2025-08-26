package com.create.chacha.domains.seller.areas.store.custom.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreCustomGetResponseDTO {
    private Long storeId;

    private FontDTO font;
    private IconDTO icon;

    private String fontColor;           // e.g. "#000000"
    private String headerFooterColor;   // e.g. "#676F58"
    private String noticeColor;         // e.g. "#FFF7DB"
    private String descriptionColor;    // e.g. "#FFF6EE"
    private String popularColor;        // e.g. "#FFF7DB"

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class FontDTO {
        private Long id;
        private String name;
        private String style;
        private String url;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class IconDTO {
        private Long id;
        private String name;
        private String content;
        private String url;
    }
}