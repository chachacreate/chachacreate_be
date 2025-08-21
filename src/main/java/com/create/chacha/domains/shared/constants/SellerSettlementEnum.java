package com.create.chacha.domains.shared.constants;

import lombok.Getter;

@Getter
public enum SellerSettlementEnum implements PersistableEnum {
    UNSETTLED(false),
    SETTLED(true);

    private final boolean value;

    SellerSettlementEnum(boolean value) {
        this.value = value;
    }

    public static SellerSettlementEnum fromValue(boolean value) {
        for (SellerSettlementEnum status : values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
