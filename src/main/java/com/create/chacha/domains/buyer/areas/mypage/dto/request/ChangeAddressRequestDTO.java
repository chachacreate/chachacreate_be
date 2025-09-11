package com.create.chacha.domains.buyer.areas.mypage.dto.request;

import lombok.Data;

@Data
public class ChangeAddressRequestDTO {
    private String postNum;
    private String addressRoad;
    private String addressDetail;
    private String addressExtra;
}
