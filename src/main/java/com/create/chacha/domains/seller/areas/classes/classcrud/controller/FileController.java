package com.create.chacha.domains.seller.areas.classes.classcrud.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.create.chacha.common.util.S3Uploader;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final S3Uploader s3Uploader;

    /**
     * 설명/본문 이미지 업로드 (원본 URL 반환)
     */
    @PostMapping(value="/upload", consumes = "multipart/form-data")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String key = s3Uploader.uploadImage(file);
            String url = s3Uploader.getFullUrl(key); // 원본 URL
            return Map.of("url", url);
        } catch (Exception e) {
            // 로깅
            // log.error("Upload failed", e);
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage(), e);
        }
    }
}
