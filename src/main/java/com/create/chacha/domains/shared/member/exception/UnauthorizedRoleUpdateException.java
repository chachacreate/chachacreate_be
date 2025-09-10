package com.create.chacha.domains.shared.member.exception;

public class UnauthorizedRoleUpdateException extends RuntimeException {
    public UnauthorizedRoleUpdateException(String message) {
        super(message);
    }

    public UnauthorizedRoleUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}