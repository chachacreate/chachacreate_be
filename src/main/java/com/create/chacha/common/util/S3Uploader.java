package com.create.chacha.common.util;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class S3Uploader {

    private final S3Client s3Client;
    private final String bucketName;
    private final long maxFileSizeBytes;

    public S3Uploader(S3Client s3Client, String bucketName, String maxFileSize) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.maxFileSizeBytes = parseSize(maxFileSize);
    }

    // -------------------- 단일 이미지 업로드 --------------------
    /**
     * 원본 WebP 변환 + 썸네일 생성 후 S3 업로드
     * @param file 업로드할 MultipartFile
     * @return String : Stringkey만 반환
     */
    public String uploadImage(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 파일명 생성
        String originalFileName = ImageUtil.generateOriginalFileName();
        String thumbnailFileName = ImageUtil.generateThumbnailFileName(originalFileName);

        // WebP 변환
        InputStream webpStream = ImageUtil.convertToWebP(file.getInputStream());

        // S3 업로드 (원본)
        String originalKey = "images/original/" + originalFileName;
        uploadToS3(webpStream, originalKey);

        // 썸네일 생성
        InputStream thumbStream = ImageUtil.createThumbnail(file.getInputStream());
        String thumbnailKey = "images/thumbnail/" + thumbnailFileName;
        uploadToS3(thumbStream, thumbnailKey);

        return originalKey;
    }

    // -------------------- 여러 이미지 업로드 --------------------
    public List<String> uploadImages(List<MultipartFile> files) throws Exception {
        List<String> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(uploadImage(file));
        }
        return results;
    }

    // -------------------- S3 삭제 --------------------
    public int delete(String key) {
        if (key == null || key.isEmpty()) return 0;
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return 1;
        } catch (Exception e) {
            System.err.println("S3 삭제 실패: " + e.getMessage());
            return 0;
        }
    }

    // -------------------- Key → Full URL --------------------
    public String getFullUrl(String key) {
        if (key == null || key.isEmpty()) return null;
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }

    // -------------------- Key → 썸네일 Full URL --------------------
    public String getThumbnailUrl(String originalKey) {
        if (originalKey == null || originalKey.isEmpty()) return null;
        String thumbnailKey = originalKey.replace("images/original/", "images/thumbnail/")
                .replace(".webp", "_thumb.webp");
        return "https://" + bucketName + ".s3.amazonaws.com/" + thumbnailKey;
    }

    // -------------------- 내부 업로드 헬퍼 --------------------
    private void uploadToS3(InputStream inputStream, String key) throws Exception {
        long contentLength = inputStream.available();
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType("image/webp")
                        .build(),
                RequestBody.fromInputStream(inputStream, contentLength)
        );
    }

    private long parseSize(String sizeStr) {
        if (sizeStr == null || sizeStr.isEmpty()) throw new IllegalArgumentException("파일 최대 크기 설정이 비어있습니다.");
        sizeStr = sizeStr.trim().toUpperCase();
        long size;
        String numberPart;
        String unit;

        if (sizeStr.endsWith("KB")) { numberPart = sizeStr.replace("KB", "").trim(); unit = "KB"; }
        else if (sizeStr.endsWith("MB")) { numberPart = sizeStr.replace("MB", "").trim(); unit = "MB"; }
        else if (sizeStr.endsWith("GB")) { numberPart = sizeStr.replace("GB", "").trim(); unit = "GB"; }
        else { numberPart = sizeStr.trim(); unit = "B"; }

        try { size = Long.parseLong(numberPart); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("잘못된 파일 크기 형식: " + sizeStr, e); }

        switch (unit) {
            case "KB": return size * 1024L;
            case "MB": return size * 1024L * 1024L;
            case "GB": return size * 1024L * 1024L * 1024L;
            case "B": return size;
            default: throw new IllegalArgumentException("알 수 없는 단위: " + unit);
        }
    }
}
