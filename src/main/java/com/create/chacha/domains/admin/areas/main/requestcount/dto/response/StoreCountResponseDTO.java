package com.create.chacha.domains.admin.areas.main.requestcount.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 관리자 메인 페이지 - 건수 응답 DTO
 */
@Getter
@AllArgsConstructor
@ToString
@Builder
public class StoreCountResponseDTO {
    private long count;
}
