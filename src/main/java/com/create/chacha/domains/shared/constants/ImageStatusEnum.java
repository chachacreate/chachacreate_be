package com.create.chacha.domains.shared.constants;

import lombok.Getter;

@Getter
public enum ImageStatusEnum implements PersistableEnum {
    DESCRIPTION(false),
    THUMBNAIL(true);

    private final boolean value;

    ImageStatusEnum(boolean value) {
        this.value = value;
    }

    public static ImageStatusEnum fromValue(boolean value) {
        for (ImageStatusEnum status : values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
