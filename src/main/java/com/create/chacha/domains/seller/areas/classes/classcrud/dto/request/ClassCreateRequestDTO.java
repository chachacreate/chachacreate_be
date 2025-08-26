package com.create.chacha.domains.seller.areas.classes.classcrud.dto.request;

import java.util.List;

import com.create.chacha.domains.shared.constants.ImageStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClassCreateRequestDTO {
	@JsonProperty("class")
    private ClassCorePayload clazz;   // "class" 키와 충돌 피하려고 이름을 clazz로 유지

    @JsonProperty("images")
    private List<ClassImagePayload> images;

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

    @Data
    public static class ClassImagePayload {
        private String url;
        private Integer imageSequence;
        private ImageStatusEnum status; // "DESCRIPTION" | "THUMBNAIL"
    }
}
