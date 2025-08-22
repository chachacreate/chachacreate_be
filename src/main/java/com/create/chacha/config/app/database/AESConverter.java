package com.create.chacha.config.app.database;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Component
@Converter(autoApply = false)
public class AESConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";

    @Value("${database.aes.secret}")
    private String secret;

    private volatile SecretKeySpec cachedKeySpec;
    private final Object keyLock = new Object();

    private SecretKeySpec getKeySpec() {
        if (cachedKeySpec == null) {
            synchronized (keyLock) {
                if (cachedKeySpec == null) {
                    initializeKey();
                }
            }
        }
        return cachedKeySpec;
    }

    private void initializeKey() {
        try {
            log.info("AES-256 키 지연 초기화 시작");
            log.debug("Secret 존재 여부: {}", secret != null && !secret.trim().isEmpty());

            if (secret == null || secret.trim().isEmpty()) {
                throw new IllegalStateException("database.aes.secret이 설정되지 않았습니다!");
            }

            // AES-256을 위한 32바이트 키 생성
            byte[] key = secret.getBytes(StandardCharsets.UTF_8);

            // SHA-256을 사용해 정확히 32바이트 키 생성 (AES-256)
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            key = sha256.digest(key);
            key = Arrays.copyOf(key, 32); // 32바이트로 설정 (AES-256)

            this.cachedKeySpec = new SecretKeySpec(key, KEY_ALGORITHM);
            log.info("AES-256 키 지연 초기화 완료 (키 길이: {} 바이트)", key.length);

        } catch (Exception e) {
            log.error("AES-256 키 초기화 실패: {}", e.getMessage(), e);
            throw new IllegalStateException("AES-256 키 초기화 실패", e);
        }
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.trim().isEmpty()) {
            return attribute;
        }

        try {
            log.debug("AES-256 암호화 시작 - 길이: {}", attribute.length());

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKeySpec());
            byte[] encryptedBytes = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));
            String encrypted = Base64.getEncoder().encodeToString(encryptedBytes);

            log.debug("AES-256 암호화 완료");
            return encrypted;

        } catch (Exception e) {
            log.error("AES-256 암호화 실패 - 원본: {}, 오류: {}", attribute, e.getMessage(), e);
            throw new IllegalStateException("AES-256 암호화 실패", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return dbData;
        }

        try {
            log.debug("AES-256 복호화 시작");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKeySpec());
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(dbData));
            String decrypted = new String(decryptedBytes, StandardCharsets.UTF_8);

            log.debug("AES-256 복호화 완료");
            return decrypted;

        } catch (Exception e) {
            log.error("AES-256 복호화 실패 - 암호화된 데이터: {}, 오류: {}", dbData, e.getMessage(), e);
            throw new IllegalStateException("AES-256 복호화 실패", e);
        }
    }
}