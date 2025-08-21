package com.create.chacha.config.app.constants;

import com.create.chacha.domains.shared.constants.PersistableEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Boolean 기반 Enum DB 매핑 컨버터
 * 모든 PersistableEnum에 대해 재사용 가능
 */
@Converter(autoApply = true)
public class GenericEnumConverter<E extends Enum<E> & PersistableEnum> implements AttributeConverter<E, Boolean> {

    private final Class<E> enumClass;

    public GenericEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Boolean convertToDatabaseColumn(E attribute) {
        return attribute != null ? attribute.isValue() : null;
    }

    @Override
    public E convertToEntityAttribute(Boolean dbData) {
        if (dbData == null) return null;

        for (E e : enumClass.getEnumConstants()) {
            if (e.isValue() == dbData) return e;
        }
        throw new IllegalArgumentException("Unknown database value: " + dbData + " for enum " + enumClass.getSimpleName());
    }
}
