package com.create.chacha.domains.seller.areas.settlement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * [드롭다운] 스토어 내 클래스 목록 응답
 */
@Getter @Builder @AllArgsConstructor @NoArgsConstructor @ToString
public class ClassOptionResponseDTO {
    private Long id;     // 클래스 ID
    private String name; // 클래스명
}
