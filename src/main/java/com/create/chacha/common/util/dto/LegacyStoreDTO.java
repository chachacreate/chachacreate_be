package com.create.chacha.common.util.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LegacyStoreDTO {
    /** 상점 고유 ID */
    private Integer storeId;

    /** 상점 소유 판매자 ID */
    private Integer sellerId;

    /** 상점 로고 이미지 경로 또는 URL */
    private String logoImg;

    /** 상점 이름 */
    private String storeName;

    /** 상점 설명 */
    private String storeDetail;

    /** 상점 고유 URL */
    private String storeUrl;

    /** 상점 총 판매 횟수 */
    private Integer saleCnt;

    /** 상점 총 조회수 */
    private Integer viewCnt;
}
