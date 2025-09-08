package com.create.chacha.domains.shared.chat.exception;

public class ChatInvalidRequestException extends RuntimeException {
    public ChatInvalidRequestException(String message) {
        super(message);
    }
}
