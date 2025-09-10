package com.create.chacha.domains.shared.member.exception;

public class InvalidMemberRoleException extends RuntimeException {
    public InvalidMemberRoleException(String message) {
        super(message);
    }

    public InvalidMemberRoleException(String message, Throwable cause) {
        super(message, cause);
    }
}
