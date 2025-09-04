package com.create.chacha.domains.seller.areas.main.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true) // 혹시라도 예상치 못한 필드가 추가돼도 무시
public class LegacyOrderStatusResponseDTO {

    @JsonProperty("ORDER_STATUS") // Legacy 응답 필드명 매핑
    private String status;

    @JsonProperty("CNT") // Legacy 응답 필드명 매핑
    private int count;
}
