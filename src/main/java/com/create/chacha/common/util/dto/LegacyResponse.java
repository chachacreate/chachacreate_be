package com.create.chacha.common.util.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LegacyResponse<T> {
    private int status;
    private String message;
    private T data;
}

