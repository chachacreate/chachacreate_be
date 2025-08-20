package com.create.chacha.common.util;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class S3Uploader {

    private final S3Client s3Client;
    private final String bucketName;
    private final long maxFileSizeBytes; // 파일 최대 크기 제한

    public S3Uploader(S3Client s3Client, String bucketName, String maxFileSize) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.maxFileSizeBytes = parseSize(maxFileSize);
    }

    // 단일 파일 업로드
    public String upload(MultipartFile file) throws Exception {
        checkFileSize(file);
        String keyName = generateUniqueFileName(file.getOriginalFilename());

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );

        return "https://" + bucketName + ".s3.amazonaws.com/" + keyName;
    }

    // 여러 파일 업로드
    public List<String> upload(List<MultipartFile> files) throws Exception {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(upload(file));
        }
        return urls;
    }

    // 경로에서 파일 이름 얻기
    public String getFileNameFromUrl(String url) {
        if (url == null) return null;
        return url.substring(url.lastIndexOf('/') + 1);
    }

    // S3에서 파일 삭제
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        // URL에서 Key 추출
        String key = getFileNameFromUrl(fileUrl);

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build()
        );
    }

    // 파일 사이즈 확인
    private void checkFileSize(MultipartFile file) {
        if (file.getSize() > maxFileSizeBytes) {
            throw new IllegalArgumentException(
                    "파일 크기가 최대 허용치를 초과했습니다: " + maxFileSizeBytes + " bytes"
            );
        }
    }

    // uuid를 사용하여 중복되지 않는 이름으로 변경
    private String generateUniqueFileName(String originalName) {
        String ext = "";
        int idx = originalName.lastIndexOf('.');
        if (idx > 0) ext = originalName.substring(idx);
        return UUID.randomUUID().toString() + ext;
    }

    // 파일 크기를 바이트 단위로 변경
    private long parseSize(String sizeStr) {
        Pattern pattern = Pattern.compile("(\\d+)([KMG]B)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sizeStr);
        if (!matcher.matches()) throw new IllegalArgumentException("잘못된 파일 크기 형식: " + sizeStr);

        long size = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2).toUpperCase();

        switch (unit) {
            case "KB": return size * 1024;
            case "MB": return size * 1024 * 1024;
            case "GB": return size * 1024 * 1024 * 1024;
            default: throw new IllegalArgumentException("알 수 없는 단위: " + unit);
        }
    }
}

