package com.create.chacha.domains.shared.chat.exception;

public class ChatMemberNotFoundException extends RuntimeException {
    public ChatMemberNotFoundException(Long memberId) {
        super("채팅 참여자를 찾을 수 없습니다: " + memberId);
    }

    public ChatMemberNotFoundException(String email) {
        super("해당 이메일의 회원을 찾을 수 없습니다: " + email);
    }
}
