package com.create.chacha.domains.shared.constants;

import lombok.Getter;

@Getter
public enum ImageStatusEnum implements PersistableEnum {
    DESCRIPTION(0),
    THUMBNAIL(1);

    private final int value;

    ImageStatusEnum(int value) {
        this.value = value;
    }

    public static ImageStatusEnum fromValue(int value) {
        for (ImageStatusEnum status : values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
