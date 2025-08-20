package com.create.chacha.config.app.constants;

import com.create.chacha.domains.shared.constants.SellerSettlementEnum;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SellerSettlementEnumConverter extends GenericEnumConverter<SellerSettlementEnum> {
    public SellerSettlementEnumConverter() {
        super(SellerSettlementEnum.class);
    }
}
