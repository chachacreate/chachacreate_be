package com.create.chacha.common.util;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S3Uploader {

    private final S3Client s3Client;
    private final String bucketName;
    private final long maxFileSizeBytes;

    public S3Uploader(S3Client s3Client, String bucketName, String maxFileSize) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.maxFileSizeBytes = parseSize(maxFileSize); // "10MB" → 바이트 변환
    }

    // 업로드 시 체크
    private void checkFileSize(InputStream inputStream) throws IOException {
        if (inputStream.available() > maxFileSizeBytes) {
            throw new IllegalArgumentException("파일 크기가 최대 허용치를 초과했습니다: " + maxFileSizeBytes + " bytes");
        }
    }

    // -------------------- 단일 이미지 업로드 --------------------
    /**
     * 원본 WebP 변환 + 썸네일 생성 후 S3 업로드
     * @param file 업로드할 원본 MultipartFile
     * @return Map { "original": 원본URL, "thumbnail": 썸네일URL }
     */
    public Map<String, String> uploadImage(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // MultipartFile → InputStream
        try (InputStream inputStream = file.getInputStream()) {

            // 파일명 생성
            String originalFileName = ImageUtil.generateOriginalFileName();
            String thumbnailFileName = ImageUtil.generateThumbnailFileName(originalFileName);

            // WebP 변환
            InputStream webpStream = ImageUtil.convertToWebP(inputStream);

            // S3 업로드 (원본)
            String originalKey = "images/original/" + originalFileName;
            String originalUrl = uploadToS3(webpStream, originalKey, "image/webp");

            // 썸네일 생성 (원본 InputStream 다시 열어야 함)
            InputStream thumbStream = ImageUtil.createThumbnail(file.getInputStream());
            String thumbnailKey = "images/thumbnail/" + thumbnailFileName;
            String thumbnailUrl = uploadToS3(thumbStream, thumbnailKey, "image/webp");

            Map<String, String> result = new HashMap<>();
            result.put("original", originalUrl);
            result.put("thumbnail", thumbnailUrl);

            return result;
        }
    }

    // -------------------- 여러 이미지 업로드 --------------------
    /**
     * 원본 WebP 변환 + 썸네일 생성 후 S3 업로드
     * @param files 업로드할 원본 List<MultipartFile>
     * @return List<Map { "original": 원본URL, "thumbnail": 썸네일URL }>
     */
    public List<Map<String, String>> uploadImages(List<MultipartFile> files) throws Exception {
        List<Map<String, String>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(uploadImage(file));
        }
        return results;
    }

    // -------------------- S3 삭제 --------------------
    /**
     * S3에서 URL 기반으로 파일 삭제
     * @param url DB에 저장된 원본 URL
     * @return 1: 삭제 성공, 0: 삭제 실패
     */
    public int delete(String url) {
        if (url == null || url.isEmpty()) return 0;

        try {
            String key = extractKeyFromUrl(url);
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build()
            );
            return 1; // 삭제 성공
        } catch (Exception e) {
            // 로그 출력 가능
            System.err.println("S3 삭제 실패: " + e.getMessage());
            return 0; // 삭제 실패
        }
    }

    private String extractKeyFromUrl(String url) {
        if (url == null || url.isEmpty()) return null;
        // https://버킷이름.s3.amazonaws.com/ 이후 경로 추출
        int idx = url.indexOf(".amazonaws.com/");
        if (idx == -1) throw new IllegalArgumentException("잘못된 S3 URL: " + url);
        return url.substring(idx + ".amazonaws.com/".length());
    }


    // -------------------- 내부 업로드 헬퍼 --------------------
    private String uploadToS3(InputStream inputStream, String key, String contentType) throws Exception {
        long contentLength = inputStream.available(); // InputStream 길이
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromInputStream(inputStream, contentLength)
        );
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }

    private long parseSize(String sizeStr) {
        if (sizeStr == null || sizeStr.isEmpty()) {
            throw new IllegalArgumentException("파일 최대 크기 설정이 비어있습니다.");
        }

        sizeStr = sizeStr.trim().toUpperCase();

        // 숫자 부분과 단위 분리
        long size;
        String numberPart;
        String unit;

        if (sizeStr.endsWith("KB")) {
            numberPart = sizeStr.replace("KB", "").trim();
            unit = "KB";
        } else if (sizeStr.endsWith("MB")) {
            numberPart = sizeStr.replace("MB", "").trim();
            unit = "MB";
        } else if (sizeStr.endsWith("GB")) {
            numberPart = sizeStr.replace("GB", "").trim();
            unit = "GB";
        } else {
            numberPart = sizeStr.trim();
            unit = "B"; // 단위 없으면 바이트
        }

        try {
            size = Long.parseLong(numberPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 파일 크기 형식: " + sizeStr, e);
        }

        // switch문으로 단위 처리
        switch (unit) {
            case "KB": return size * 1024L;
            case "MB": return size * 1024L * 1024L;
            case "GB": return size * 1024L * 1024L * 1024L;
            case "B":  return size;
            default: throw new IllegalArgumentException("알 수 없는 단위: " + unit);
        }
    }

}
