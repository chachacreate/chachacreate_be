package com.create.chacha.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64 인코딩/디코딩 유틸리티 클래스
 * 한글 및 특수문자가 포함된 문자열을 안전하게 URL에서 사용할 수 있도록 처리
 */
@Component
public class Base64Util {

    private static final Logger logger = LoggerFactory.getLogger(Base64Util.class);

    /**
     * 문자열을 Base64로 인코딩
     * @param text 인코딩할 문자열
     * @return Base64 인코딩된 문자열
     */
    public static String encode(String text) {
        if (text == null || text.isEmpty()) {
            logger.warn("Base64 인코딩: 입력 문자열이 null 또는 빈 문자열입니다.");
            return "";
        }

        try {
            byte[] encodedBytes = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encode(text.getBytes(StandardCharsets.UTF_8));
            String result = new String(encodedBytes, StandardCharsets.UTF_8);
            logger.debug("Base64 인코딩 성공: {} -> {}", text, result);
            return result;
        } catch (Exception e) {
            logger.error("Base64 인코딩 실패: {}", text, e);
            return text; // 실패 시 원본 반환
        }
    }

    /**
     * Base64 문자열을 디코딩
     * @param encodedText Base64 인코딩된 문자열
     * @return 디코딩된 원본 문자열
     */
    public static String decode(String encodedText) {
        if (encodedText == null || encodedText.isEmpty()) {
            logger.warn("Base64 디코딩: 입력 문자열이 null 또는 빈 문자열입니다.");
            return "";
        }

        try {
            // URL Safe Base64 디코딩 시도
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedText);
            String result = new String(decodedBytes, StandardCharsets.UTF_8);
            logger.debug("Base64 디코딩 성공: {} -> {}", encodedText, result);
            return result;
        } catch (IllegalArgumentException e) {
            // URL Safe 디코딩 실패 시 일반 Base64 디코딩 시도
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
                String result = new String(decodedBytes, StandardCharsets.UTF_8);
                logger.debug("일반 Base64 디코딩 성공: {} -> {}", encodedText, result);
                return result;
            } catch (Exception ex) {
                logger.error("Base64 디코딩 실패: {}", encodedText, ex);
                return encodedText; // 실패 시 원본 반환
            }
        } catch (Exception e) {
            logger.error("Base64 디코딩 오류: {}", encodedText, e);
            return encodedText; // 실패 시 원본 반환
        }
    }

    /**
     * 문자열이 Base64로 인코딩된 것인지 확인
     * @param text 확인할 문자열
     * @return Base64 인코딩된 문자열이면 true
     */
    public static boolean isBase64Encoded(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        try {
            // URL Safe Base64 형식 확인
            Base64.getUrlDecoder().decode(text);
            return true;
        } catch (IllegalArgumentException e) {
            try {
                // 일반 Base64 형식 확인
                Base64.getDecoder().decode(text);
                return true;
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }
    }

    /**
     * 채팅방 ID 전용 인코딩 (로깅 포함)
     * @param chatroomId 채팅방 ID
     * @return 인코딩된 채팅방 ID
     */
    public static String encodeChatroomId(String chatroomId) {
        logger.info("채팅방 ID 인코딩: {}", chatroomId);
        return encode(chatroomId);
    }

    /**
     * 채팅방 ID 전용 디코딩 (로깅 포함)
     * @param encodedChatroomId 인코딩된 채팅방 ID
     * @return 디코딩된 채팅방 ID
     */
    public static String decodeChatroomId(String encodedChatroomId) {
        String decoded = decode(encodedChatroomId);
        logger.info("채팅방 ID 디코딩: {} -> {}", encodedChatroomId, decoded);
        return decoded;
    }

    /**
     * 안전한 디코딩 (예외 발생하지 않음)
     * @param encodedText 인코딩된 텍스트
     * @param defaultValue 디코딩 실패 시 반환할 기본값
     * @return 디코딩된 문자열 또는 기본값
     */
    public static String safeDecodeWithDefault(String encodedText, String defaultValue) {
        try {
            String decoded = decode(encodedText);
            return decoded != null && !decoded.equals(encodedText) ? decoded : defaultValue;
        } catch (Exception e) {
            logger.warn("안전한 디코딩 실패, 기본값 반환: {} -> {}", encodedText, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 자동 인코딩/디코딩 (문자열 상태에 따라 자동 처리)
     * @param text 처리할 문자열
     * @return 적절하게 처리된 문자열
     */
    public static String autoProcess(String text) {
        if (isBase64Encoded(text)) {
            return decode(text);
        } else {
            return encode(text);
        }
    }
}