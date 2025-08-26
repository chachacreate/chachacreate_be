package com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response;

import lombok.*;

@Getter @Builder @AllArgsConstructor
public class ClassSummaryResponseDTO {
	
    private Long classId;
    private String title;
    private String description;
    private Integer price;

    // 주소
    private String postNum;
    private String addressRoad;
    private String addressDetail;
    private String addressExtra;

    // 스토어
    private Long storeId;
    private String storeName;
}
