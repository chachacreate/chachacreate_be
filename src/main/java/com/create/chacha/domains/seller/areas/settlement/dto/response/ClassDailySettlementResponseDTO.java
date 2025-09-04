package com.create.chacha.domains.seller.areas.settlement.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 특정 클래스의 대표이미지 + 일별 결제금액 응답
 */
@Getter @Builder @AllArgsConstructor @NoArgsConstructor @ToString
public class ClassDailySettlementResponseDTO {
    private Long classId;              	// 클래스 ID
    private String className;          	// 클래스명
    private String thumbnailUrl;       	// 대표이미지 1개
    private List<DailyEntry> daily;    // 일자/금액 목록

    @Getter @Builder @AllArgsConstructor @NoArgsConstructor @ToString
    public static class DailyEntry {
        private String date;   // YYYY-MM-DD
        private Integer amount; // 해당일 결제 합계
    }
}
