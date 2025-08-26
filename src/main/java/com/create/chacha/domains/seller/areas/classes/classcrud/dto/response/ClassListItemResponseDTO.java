package com.create.chacha.domains.seller.areas.classes.classcrud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ClassListItemResponseDTO {
    private Long classId;            // 클래스 ID
    private String thumbnailUrl;     // 대표 썸네일 URL (THUMBNAIL & seq=1)
    private String title;            // 클래스 이름
    private String location;         // postNum addressRoad addressDetail addressExtra (스페이스 1칸)
    private Integer participant;     // 최대 참여 인원
    private Integer price;           // 회당 가격
    private String period;           // "yyyy-MM-dd ~ yyyy-MM-dd"
    private LocalDateTime createdAt; // 등록일
    private LocalDateTime updatedAt; // 최근 수정일
    private LocalDateTime deletedAt; // 최근 삭제일 (없으면 null)
    private Boolean isDeleted;       // 삭제 여부(프론트 체크박스 표시용)
}