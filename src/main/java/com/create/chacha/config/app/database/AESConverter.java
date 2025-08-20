package com.create.chacha.config.app.database;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
@Converter(autoApply = false) // 특정 컬럼에만 적용 → autoApply=true 하면 모든 String 컬럼에 적용됨
public class AESConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    @Value("${database.aes.secret}")
    private String secret;

    private SecretKeySpec getKeySpec() {
        return new SecretKeySpec(secret.getBytes(), "AES"); // Base64 디코딩 제거 (이미 디코딩된 값)
    }

    // 데이터베이스 입력 시 암호화
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM); // 암호화 객체 생성
            cipher.init(Cipher.ENCRYPT_MODE, getKeySpec()); // 암호화 모드로 초기화, 키 설정
            byte[] encryptedBytes = cipher.doFinal(attribute.getBytes("UTF-8")); // 암호화
            return Base64.getEncoder().encodeToString(encryptedBytes); // 암호화한 객체를 Base64 인코딩된 문자열로 반환
        } catch (Exception e) {
            throw new IllegalStateException("AES 암호화 실패", e);
        }
    }

    // 데이터베이스 출력 시 암호화
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKeySpec()); // 복호화 객체 생성
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(dbData)); // Base64로 인코딩된 문자열을 변환
            return new String(decryptedBytes, "UTF-8"); // UTF-8로 문자열 인코딩
        } catch (Exception e) {
            throw new IllegalStateException("AES 복호화 실패", e);
        }
    }
}
