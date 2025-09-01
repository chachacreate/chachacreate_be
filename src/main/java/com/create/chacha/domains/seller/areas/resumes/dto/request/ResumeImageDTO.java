package com.create.chacha.domains.seller.areas.resumes.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResumeImageDTO {
    private String content; // 설명 (nullable)
    private MultipartFile file; // 이미지 파일
}
