package com.create.chacha.config.app.constants;

import com.create.chacha.domains.shared.constants.PersistableEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 숫자형 Enum DB 매핑 컨버터
 * <p>
 * 모든 PersistableEnum에 대해 재사용 가능
 * </p>
 *
 * @param <E> PersistableEnum 타입
 */
@Converter(autoApply = true)
public class GenericEnumConverter<E extends Enum<E> & PersistableEnum> implements AttributeConverter<E, Integer> {

    private final Class<E> enumClass;

    public GenericEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Integer convertToDatabaseColumn(E attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public E convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;

        try {
            for (E e : enumClass.getEnumConstants()) {
                if (e.getValue() == dbData) return e;
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unknown database value: " + dbData + " for enum " + enumClass.getSimpleName());
        }

        throw new IllegalArgumentException("Unknown database value: " + dbData + " for enum " + enumClass.getSimpleName());
    }
}
