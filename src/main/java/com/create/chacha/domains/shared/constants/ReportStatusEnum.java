package com.create.chacha.domains.shared.constants;

import lombok.Getter;

@Getter
public enum ReportStatusEnum implements PersistableEnum{
    UNPROCESSED(0),
    PROCESSED(1);

    private final int value;

    ReportStatusEnum(int value) {
        this.value = value;
    }

    public static ReportStatusEnum fromValue(int value) {
        for (ReportStatusEnum status : values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
