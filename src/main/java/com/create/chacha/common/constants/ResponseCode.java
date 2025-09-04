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
    ADMIN_STORE_COUNT_FOUND(200, "스토어 개설 요청 건수 조회 성공"),
	ADMIN_STORE_COUNT_NOT_FOUND(404, "스토어 개설 요청 건수를 찾을 수 없습니다."),
	ADMIN_RESUME_COUNT_FOUND(200, "이력서 신청 건수 조회 성공"),
	ADMIN_RESUME_COUNT_NOT_FOUND(404, "이력서 신청 건수를 찾을 수 없습니다."),

    //// main & buyer
    RESERVATION_SUCCESS(201, "클래스 예약에 성공했습니다."),
    PAYMENT_FAIL(402, "결제 승인에 실패했습니다."),
    RESERVATION_FAIL(400, "예약 처리 중 오류가 발생했습니다. 로그인 혹은 클래스 정보를 확인해 주세요."),
    RESERVATION_SAVE_FAIL(500, "예약 저장 중 오류가 발생했습니다."),
    
    PRODUCTS_FOUND(200, "스토어 전체 상품 조회 성공"),
    PRODUCTS_NOT_FOUND(404, "스토어 상품을 찾을 수 없습니다."),
    
    CLASSES_FOUND(200, "전체 클래스 목록 조회 성공"),
    CLASSES_NOT_FOUND(404, "클래스 목록을 찾을 수 없습니다."),
    CLASSES_AVAILABLE_FOUND(200, "예약 가능 클래스 조회 성공"),
    CLASSES_AVAILABLE_NOT_FOUND(404, "예약 가능한 클래스가 없습니다."),
    
    MEMBER_RESERVATIONS_FOUND(200, "회원 예약 내역 조회 성공"),
    MEMBER_RESERVATIONS_NOT_FOUND(404, "해당 회원의 예약 내역이 없습니다."),
  
    CLASS_SAMMARY_OK(200, "요청이 성공적으로 처리되었습니다."),
    CLASS_IMAGES_OK(200, "요청이 성공적으로 처리되었습니다."),
    CLASS_SCHEDULE_OK(200, "요청이 성공적으로 처리되었습니다."),
    
    STORE_POPULAR_PRODUCTS_FOUND(200, "스토어 인기상품 조회 성공"),
    STORE_POPULAR_PRODUCTS_NOT_FOUND(404, "스토어 인기상품이 없습니다."),
    
    STORE_FLAGSHIP_PRODUCTS_FOUND(200, "스토어 대표상품 조회 성공"),
    STORE_FLAGSHIP_PRODUCTS_NOT_FOUND(404, "스토어 대표상품이 없습니다."),
    
    STORE_ALL_PRODUCTS_FOUND(200, "스토어 전체상품 조회 성공"),
    STORE_ALL_PRODUCTS_NOT_FOUND(404, "스토어 전체상품을 찾을 수 없습니다."),
    
    PRODUCT_TYPE_INVALID(400, "지원하지 않는 type 값입니다. [popular|flagship]만 허용됩니다."),
    
    ////seller
    SELLER_MAIN_STATUS_OK(200, "요청이 성공적으로 처리되었습니다."),
    SELLER_SETTLEMENT_FOUND(200, "판매자 정산 내역 조회에 성공했습니다."),
    SELLER_SETTLEMENT_NOT_FOUND(404, "판매자 정산 내역을 찾을 수 없습니다."),

    RESUME_UPLOAD_SUCCESS(201, "판매자 이력 파일 업로드가 완료되었습니다."),
    RESUME_UPLOAD_FAIL(400, "판매자 이력 파일 업로드가 실패하였습니다"),
    
	////seller (classes CRUD)
	SELLER_CLASS_UPDATE_OK(200, "클래스가 성공적으로 수정되었습니다."),
	SELLER_CLASS_FORM_FOUND(200, "클래스 수정 폼 조회 성공"),
	SELLER_CLASS_FORM_NOT_FOUND(404, "클래스 수정 폼 대상을 찾을 수 없습니다."),
	
	SELLER_CLASS_TOGGLE_OK(200, "클래스 삭제/복구가 완료되었습니다."),
	SELLER_CLASS_TOGGLE_PARTIAL(206, "일부 클래스만 삭제/복구에 성공했습니다."),
	SELLER_CLASS_TOGGLE_NOT_FOUND(404, "삭제/복구 대상 클래스를 찾을 수 없습니다."),
	SELLER_CLASS_TOGGLE_BAD_REQUEST(400, "클래스 ID 목록이 비어있습니다."),
	
	SELLER_CLASSES_FOUND(200, "스토어 클래스 목록 조회 성공"),
	SELLER_CLASSES_NOT_FOUND(404, "스토어 클래스 목록이 없습니다."),
	
	SELLER_CLASS_CREATE_CREATED(201, "클래스가 성공적으로 생성되었습니다."),
	SELLER_CLASS_CREATE_BAD_REQUEST(400, "클래스 생성 요청이 올바르지 않습니다."),
	SELLER_CLASS_CREATE_PARTIAL(206, "일부 클래스만 생성되었습니다."),
	
	////seller (product price)
	SELLER_PRICE_PREVIEW_OK(200, "상품 가격 추천(모의)에 성공했습니다."),
	SELLER_PRICE_PREVIEW_BAD_REQUEST(400, "이미지 파일은 정확히 3개여야 합니다."),
	SELLER_PRICE_PREVIEW_UNSUPPORTED(415, "이미지 파일만 업로드할 수 있습니다."),
	SELLER_PRICE_PREVIEW_ERROR(500, "가격 추천 처리 중 오류가 발생했습니다."),
	
	////seller (reviews)
	SELLER_REVIEW_STATS_FOUND(200, "리뷰 통계 조회 성공"),
	SELLER_REVIEW_STATS_NOT_FOUND(404, "리뷰 통계를 찾을 수 없습니다."),
	SELLER_PRODUCT_REVIEW_STATS_FOUND(200, "상품 리뷰 통계 조회 성공"),
	SELLER_PRODUCT_REVIEW_STATS_NOT_FOUND(404, "상품 리뷰 통계를 찾을 수 없습니다."),
	
	SELLER_REVIEWS_FOUND(200, "리뷰 목록 조회 성공"),
	SELLER_REVIEWS_NOT_FOUND(404, "리뷰가 없습니다."),
	SELLER_PRODUCT_REVIEWS_FOUND(200, "상품 리뷰 목록 조회 성공"),
	SELLER_PRODUCT_REVIEWS_NOT_FOUND(404, "해당 상품의 리뷰가 없습니다."),
	
	// seller (store custom)
	SELLER_STORE_CUSTOM_FOUND(200, "스토어 커스텀 조회 성공"),
	SELLER_STORE_CUSTOM_NOT_FOUND(404, "스토어 커스텀 설정이 없습니다."),
	SELLER_STORE_CUSTOM_UPDATED(200, "스토어 커스텀 수정 성공"),
	SELLER_STORE_CUSTOM_BAD_REQUEST(400, "스토어 커스텀 요청이 올바르지 않습니다."),

    CLASS_RESERVATIONS_FOUND(200, "클래스 예약 현황 조회 성공"),
    CLASS_RESERVATIONS_NOT_FOUND(404, "해당 클래스의 예약 내역이 없습니다."),
    CLASS_STATS_FOUND(200, "클래스 예약 통계 조회 성공"),
    CLASS_STATS_NOT_FOUND(404, "예약 통계를 찾을 수 없습니다."),
    
    SELLER_RESERVATION_STATS_OK(200, "판매자 예약 통계 조회 성공"),
    SELLER_RESERVATION_STATS_NOT_FOUND(404, "판매자 예약 통계를 찾을 수 없습니다."),
    
    SELLER_RESERVATION_STATS_BAD_SCOPE(400, "scope=class 인 경우 classId가 필요합니다."),
    SELLER_RESERVATION_GROUPBY_INVALID(400, "groupBy/dimension 값이 올바르지 않습니다."),

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
