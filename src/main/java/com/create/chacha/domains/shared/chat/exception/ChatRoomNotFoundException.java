package com.create.chacha.domains.shared.chat.exception;

public class ChatRoomNotFoundException extends RuntimeException {
    public ChatRoomNotFoundException(String chatroomId) {
        super("채팅방을 찾을 수 없습니다: " + chatroomId);
    }
}
