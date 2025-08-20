package com.create.chacha.config.app.constants;

import com.create.chacha.domains.shared.constants.ImageStatusEnum;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ImageStatusEnumConverter extends GenericEnumConverter<ImageStatusEnum> {
    public ImageStatusEnumConverter() {
        super(ImageStatusEnum.class);
    }
}
