package com.create.chacha.domains.shared.chat.exception;

public class ChatAccessDeniedException extends RuntimeException {
    public ChatAccessDeniedException(String message) {
        super(message);
    }
}
