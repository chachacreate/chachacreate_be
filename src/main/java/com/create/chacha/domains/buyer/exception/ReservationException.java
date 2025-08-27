package com.create.chacha.domains.buyer.exception;

import com.create.chacha.common.constants.ResponseCode;

public class ReservationException extends RuntimeException{
    public ReservationException(String message) {
        super(message);
    }
}
