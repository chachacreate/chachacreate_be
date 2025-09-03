package com.create.chacha.domains.seller.areas.classes.classcrud.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ClassCreateRequestDTO {

    private ClassCorePayload clazz;

    // 썸네일은 정확히 3장
    private MultipartFile[] thumbnails;

    // 설명 이미지는 0..N장
    private MultipartFile[] descriptions;

    @Data
    public static class ClassCorePayload {
        private String title;
        @Column(columnDefinition = "LONGTEXT")
        private String detail;
        // 최종 HTML에 남아있는 이미지(저장 대상)
        private List<String> detailImageUrls;

        // (신규) 에디터 사용 중 업로드 되었던 전체 URL(프론트가 보낸 목록)
        // detailImageUrls 에 없는 것들은 S3에서 삭제
        private List<String> editorUploadedUrls;
        
        private Integer price;
        private String guideline;

        private Integer participant;

        private String postNum;
        private String addressRoad;
        private String addressDetail;
        private String addressExtra;

        // "yyyy-MM-dd HH:mm:ss"
        private String startDate;
        private String endDate;

        // "HH:mm:ss"
        private String startTime;
        private String endTime;

        // 분 단위
        private Integer timeInterval;
    }
}
