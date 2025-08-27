package com.create.chacha.common.constants;

public enum ResponseCode {
    // 1xx: Informational
    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    PROCESSING(102, "Processing"),

    // 2xx: Success
    OK(200, "요청이 성공적으로 처리되었습니다."),
    CREATED(201, "리소스가 성공적으로 생성되었습니다."),
    ACCEPTED(202, "요청이 수락되었습니다."),
    NO_CONTENT(204, "응답할 콘텐츠가 없습니다."),
    PARTIAL_CONTENT(206, "Partial Content"),

    // 3xx: Redirection
    MOVED_PERMANENTLY(301, "요청한 리소스가 영구적으로 이동되었습니다."),
    FOUND(302, "요청한 리소스가 임시로 다른 URI에 있습니다."),
    NOT_MODIFIED(304, "리소스가 수정되지 않았습니다."),

    // 4xx: Client Error
    BAD_REQUEST(400, "잘못된 요청입니다."),
    FAIL(400, "요청 처리에 실패했습니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    SESSION_EXPIRED(401, "세션이 만료되었습니다."),
    FORBIDDEN(403, "접근이 금지되었습니다."),
    NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(405, "허용되지 않는 HTTP 메서드입니다."),
    CONFLICT(409, "요청이 서버와 충돌했습니다."),
    UNSUPPORTED_MEDIA_TYPE(415, "지원하지 않는 미디어 타입입니다."),
    TOO_MANY_REQUESTS(429, "요청이 너무 많습니다. 나중에 다시 시도하세요."),

    // 5xx: Server Error
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    NOT_IMPLEMENTED(501, "구현되지 않은 기능입니다."),
    BAD_GATEWAY(502, "잘못된 게이트웨이입니다."),
    SERVICE_UNAVAILABLE(503, "서비스를 사용할 수 없습니다."),
    GATEWAY_TIMEOUT(504, "게이트웨이 시간 초과입니다."),

    //// store


    //// admin


    //// main & buyer
    CLASS_SAMMARY_OK(200, "요청이 성공적으로 처리되었습니다."),

    //// login & message
    // message

    // login
    LOGOUT_SUCCESS(200,"로그아웃에 성공했습니다."),
    LOGOUT_FAIL(400,"로그아웃에 실패했습니다. 로그인이 되어있었는지 확인해주세요."),
    REFRESH_SUCCESS(200,"Access Token 재발급에 성공했습니다."),
    REFRESH_FAIL(401,"Access Token 재발급에 실패했습니다. Refresh Token을 확인해주세요."),
    REGISTER_SUCCESS(201,"회원 가입에 성공했습니다."),
    REGISTER_FAIL(400, "회원 가입에 실패했습니다. 정보가 다 입력되었는지 확인해주세요."),
    LOGIN_SUCCESS(200,"로그인에 성공했습니다."),
    LOGIN_FAIL(401, "로그인에 실패했습니다. 회원 아이디나 비밀번호를 확인해주세요");

    private final int status;
    private final String message;

    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
