package com.create.chacha.domains.shared.constants;

import lombok.Getter;

@Getter
public enum ReportStatusEnum implements PersistableEnum {
    UNPROCESSED(false),
    PROCESSED(true);

    private final boolean value;

    ReportStatusEnum(boolean value) {
        this.value = value;
    }

    public static ReportStatusEnum fromValue(boolean value) {
        for (ReportStatusEnum status : values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
