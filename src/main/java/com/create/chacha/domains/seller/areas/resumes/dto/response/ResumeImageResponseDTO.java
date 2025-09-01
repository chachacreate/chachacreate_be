package com.create.chacha.domains.seller.areas.resumes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeImageResponseDTO {
    private Long id;       // 이미지 ID
    private String url;    // 업로드된 이미지 URL
    private String content; // 이미지 설명
}