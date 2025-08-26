package com.create.chacha.domains.seller.areas.classes.classcrud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ClassUpdateResponseDTO {
    private Long classId;
    private int updatedThumbnailCount;   // 교체/신규 반영된 썸네일 수(최대 3)
    private int addedDescriptionCount;   // 새로 추가된 설명 이미지 수
    private int deletedDescriptionCount; // 논리삭제된 설명 이미지 수
}
