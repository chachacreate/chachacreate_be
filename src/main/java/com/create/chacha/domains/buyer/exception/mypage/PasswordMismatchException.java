package com.create.chacha.domains.buyer.exception.mypage;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}