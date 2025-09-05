package com.create.chacha.domains.buyer.exception.payment;

public class PaymentRequestException extends RuntimeException {
    public PaymentRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
