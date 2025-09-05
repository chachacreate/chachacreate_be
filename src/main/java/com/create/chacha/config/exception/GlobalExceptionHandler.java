package com.create.chacha.config.exception;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.common.exception.DatabaseException;
import com.create.chacha.domains.buyer.exception.*;
import com.create.chacha.domains.buyer.exception.mypage.MemberUpdateException;
import com.create.chacha.domains.buyer.exception.mypage.PasswordMismatchException;
import com.create.chacha.domains.buyer.exception.mypage.PasswordValidationException;
import com.create.chacha.domains.seller.areas.resumes.exception.ResumeUploadException;
import com.create.chacha.domains.shared.member.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 로그인 예외
    @ExceptionHandler(InvalidPasswordException.class)
    public ApiResponse<String> handlePasswordException(InvalidPasswordException e) {
        log.warn("비밀번호 오류: {}", e.getMessage());
        return new ApiResponse<>(ResponseCode.LOGIN_FAIL, e.getMessage());
    }
    // 회원가입 관련 예외들
    @ExceptionHandler(DuplicateEmailException.class)
    public ApiResponse<String> handleDuplicateEmail(DuplicateEmailException e) {
        log.warn("이메일 중복: {}", e.getMessage());
        return new ApiResponse<>(ResponseCode.REGISTER_FAIL, e.getMessage());
    }

    @ExceptionHandler({InvalidRequestException.class})
    public ApiResponse<String> handleBadRequest(RuntimeException e) {
        log.warn("잘못된 요청: {}", e.getMessage());
        return new ApiResponse<>(ResponseCode.REGISTER_FAIL, e.getMessage());
    }

    @ExceptionHandler({MemberAddressNotFoundException.class, MemberSaveException.class})
    public ApiResponse<String> handleMemberError(RuntimeException e) {
        log.error("회원 처리 오류: {}", e.getMessage());
        return new ApiResponse<>(ResponseCode.REGISTER_FAIL, "회원 정보 처리 중 오류가 발생했습니다.");
    }

    @ExceptionHandler(MemberRegistrationException.class)
    public ApiResponse<String> handleMemberRegistration(MemberRegistrationException e) {
        log.error("회원가입 처리 오류: {}", e.getMessage());
        return new ApiResponse<>(ResponseCode.REGISTER_FAIL,"회원가입 처리 중 오류가 발생했습니다.");
    }

    @ExceptionHandler(DatabaseException.class)
    public ApiResponse<String> handleDatabaseError(DatabaseException e) {
        log.error("데이터베이스 오류: {}", e.getMessage());
        return new ApiResponse<>(ResponseCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    }

    // 마이페이지
    @ExceptionHandler(PasswordValidationException.class)
    public ApiResponse<String> handleInvalidPassword(PasswordValidationException e) {
        log.warn("비밀번호 정책 위반: {}", e.getMessage());
        return new ApiResponse<>(ResponseCode.PASSWORD_INVALID, e.getMessage());
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ApiResponse<String> handlePasswordMismatch(PasswordMismatchException e) {
        log.warn("비밀번호 불일치: {}", e.getMessage());
        return new ApiResponse<>(ResponseCode.PASSWORD_MISMATCH, e.getMessage());
    }

    @ExceptionHandler(MemberUpdateException.class)
    public ApiResponse<String> handleMemberUpdateFail(MemberUpdateException e) {
        log.warn("회원 정보 수정 실패: {}", e.getMessage());
        return new ApiResponse<>(ResponseCode.MEMBER_UPDATE_FAIL, e.getMessage());
    }


    // 결제/예약 관련 예외 처리
    @ExceptionHandler(PaymentFailedException.class)
    public ApiResponse<String> handlePaymentFailed(PaymentFailedException e) {
        log.warn("결제 실패: {}", e.getMessage());
        return new ApiResponse<>(ResponseCode.PAYMENT_FAIL, e.getMessage());
    }

    @ExceptionHandler(PaymentRequestException.class)
    public ApiResponse<String> handlePaymentRequestError(PaymentRequestException e) {
        log.error("결제 요청 중 오류 발생", e);
        return new ApiResponse<>(ResponseCode.PAYMENT_FAIL, e.getMessage());
    }

    @ExceptionHandler(ReservationException.class)
    public ApiResponse<String> handleReservationError(ReservationException e) {
        log.error("예약 처리 중 오류 발생", e);
        return new ApiResponse<>(ResponseCode.RESERVATION_FAIL, e.getMessage());
    }

    @ExceptionHandler(ReservationSaveException.class)
    public ApiResponse<String> handleReservationSaveError(ReservationSaveException e) {
        log.error("예약 저장 중 오류 발생", e);
        return new ApiResponse<>(ResponseCode.RESERVATION_SAVE_FAIL, e.getMessage());
    }


    // seller
    @ExceptionHandler(ResumeUploadException.class)
    public ApiResponse<String> handleResumeUploadError(ResumeUploadException e) {
        log.error("판매자 이력 인증 중 에러 발생", e);
        return new ApiResponse<>(ResponseCode.RESUME_UPLOAD_FAIL, e.getMessage());
    }



    // 일반적인 예외 처리 (catch-all)
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleGenericException(Exception e) {
        log.error("예상치 못한 오류: {}", e.getMessage(), e);
        return new ApiResponse<>(ResponseCode.INTERNAL_SERVER_ERROR, "시스템 오류가 발생했습니다.");
    }
}