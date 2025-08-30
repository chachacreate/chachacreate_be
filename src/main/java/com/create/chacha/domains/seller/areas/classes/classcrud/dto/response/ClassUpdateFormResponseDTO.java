package com.create.chacha.domains.seller.areas.classes.classcrud.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class ClassUpdateFormResponseDTO {
    private Core core;
    private List<ImageItem> thumbnails;   // seq=1..3
    private List<ImageItem> descriptions; // seq=1..N

    @Data
    public static class Core {
        private String title;
        private String detail;
        private Integer price;
        private String guideline;
        private Integer participant;
        private String postNum;
        private String addressRoad;
        private String addressDetail;
        private String addressExtra;
        private String startDate;  // yyyy-MM-dd HH:mm:ss
        private String endDate;    // yyyy-MM-dd HH:mm:ss
        private String startTime;  // HH:mm:ss
        private String endTime;    // HH:mm:ss
        private Integer timeInterval;
    }

    @Data
    public static class ImageItem {
        private String url;
        private Integer imageSequence;
    }
}