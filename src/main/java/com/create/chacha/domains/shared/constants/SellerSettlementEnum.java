package com.create.chacha.domains.shared.constants;

import lombok.Getter;

@Getter
public enum SellerSettlementEnum implements PersistableEnum {
    UNSETTLED(0),
    SETTLED(1);

    private final int value;

    SellerSettlementEnum(int value) {
        this.value = value;
    }

    public static SellerSettlementEnum fromValue(int value) {
        for (SellerSettlementEnum status : values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
