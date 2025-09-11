package com.create.chacha.domains.buyer.areas.mypage.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeAddressResponseDTO {
    private String postNum;
    private String addressRoad;
    private String addressDetail;
    private String addressExtra;
}
