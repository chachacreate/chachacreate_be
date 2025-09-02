package com.create.chacha.domains.seller.areas.classes.classcrud.dto.request;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class ClassUpdateRequestDTO {

    // 본문은 부분 수정이므로 통째로 or 일부만 보낼 수 있음(선택)
    private ClassCorePayload clazz;

    // ====== 이미지 부분 교체 전용 필드 ======
    // 썸네일은 1~3 중 필요한 seq만 골라 교체
    private MultipartFile[] thumbnails;   // files (선택)

    // 설명 이미지는 "대상 seq만 논리삭제 + 새 이미지 append(max+1)"
    private MultipartFile[] descriptions;       // files (선택)
    private Integer[] replaceDescriptionSeqs;   // descriptions 와 1:1 매핑 (예: [2,5,7])

    @Data
    public static class ClassCorePayload {
        private String title;
        private String detail;
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
