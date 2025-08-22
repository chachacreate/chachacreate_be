package com.create.chacha.common;

import com.create.chacha.common.constants.ResponseCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public ApiResponse(ResponseCode code, T data) {
        this.status = code.getStatus();
        this.message = code.getMessage();
        this.data = data;
    }
}
