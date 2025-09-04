package com.create.chacha.domains.shared.member.exception;

/**
 * 회원을 찾을 수 없을 때 발생하는 예외
 */
public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException() {
        super("회원을 찾을 수 없습니다.");
    }

    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberNotFoundException(Long memberId) {
        super("회원을 찾을 수 없습니다. ID: " + memberId);
    }

    public MemberNotFoundException(String email, boolean isEmail) {
        super("회원을 찾을 수 없습니다. Email: " + email);
    }
}
