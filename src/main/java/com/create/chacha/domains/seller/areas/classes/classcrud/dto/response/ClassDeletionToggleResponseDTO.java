package com.create.chacha.domains.seller.areas.classes.classcrud.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ClassDeletionToggleResponseDTO {
    private Integer requestedCount;
    private Integer toggledToDeletedCount;   // 0->1 로 바뀐 개수
    private Integer toggledToRestoredCount;  // 1->0 로 바뀐 개수
    private List<Long> toggledToDeletedIds;
    private List<Long> toggledToRestoredIds;
    private List<Long> notFoundOrMismatchedIds; // 스토어 소속 아님 / 미존재
}