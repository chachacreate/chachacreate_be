package com.create.chacha.common.util;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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

    // InputStream → 썸네일 생성
    public static InputStream createThumbnail(InputStream inputStream) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        if (image == null) throw new IllegalArgumentException("이미지를 읽을 수 없습니다.");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnails.of(image)
                .size(300, 300) // 섬네일 크기 조정을 원하면 이 파라미터 변경
                .outputFormat("webp")
                .toOutputStream(os);
        return new ByteArrayInputStream(os.toByteArray());
    }
}
