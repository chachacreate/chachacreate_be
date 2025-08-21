package com.create.chacha.common.util;

import net.coobird.thumbnailator.Thumbnails;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImageUtil {

    // 원본 파일명 생성 (UUID.webp)
    public static String generateOriginalFileName() {
        return UUID.randomUUID().toString() + ".webp";
    }

    // 썸네일 파일명 생성 (UUID_thumb.webp)
    public static String generateThumbnailFileName(String originalFileName) {
        return originalFileName.replace(".webp", "_thumb.webp");
    }

    // InputStream → WebP 변환
    public static InputStream convertToWebP(InputStream inputStream) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        if (image == null) throw new IllegalArgumentException("이미지를 읽을 수 없습니다.");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "webp", os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    // InputStream → 썸네일 생성 (300px 기준)
    public static InputStream createThumbnail(InputStream inputStream) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        if (image == null) throw new IllegalArgumentException("이미지를 읽을 수 없습니다.");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnails.of(image)
                .size(300, 300)
                .outputFormat("webp")
                .toOutputStream(os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    /**
     * 원본과 썸네일 URL을 반환하는 Map
     * { "original": 원본URL, "thumbnail": 썸네일URL }
     */
    public static Map<String, String> getImageUrls(String originalKey, String thumbnailKey, String bucketName) {
        Map<String, String> urls = new HashMap<>();
        urls.put("original", "https://" + bucketName + ".s3.amazonaws.com/" + originalKey);
        urls.put("thumbnail", "https://" + bucketName + ".s3.amazonaws.com/" + thumbnailKey);
        return urls;
    }
}
