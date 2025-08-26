package com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response;

import lombok.*;
import java.util.List;

@Getter @Builder @AllArgsConstructor
public class ClassImagesResponseDTO {
    private Long classId;
    private List<Image> images;

    @Getter @Builder @AllArgsConstructor
    public static class Image {
        private String url;
        private String thumbnailUrl;
        private Integer sequence;
    }
}
