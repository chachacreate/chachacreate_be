package com.create.chacha.domains.shared.constants;

/**
 * DB에 TinyInt(=boolean)로 저장되는 Enum을 위한 공통 인터페이스
 */
public interface PersistableEnum {
    boolean isValue();
}
