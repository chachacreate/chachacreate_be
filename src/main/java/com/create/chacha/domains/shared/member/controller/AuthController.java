package com.create.chacha.domains.shared.member.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.member.dto.request.LoginRequestDTO;
import com.create.chacha.domains.shared.member.dto.request.RegisterRequestDTO;
import com.create.chacha.domains.shared.member.dto.response.AuthValidationResponseDTO;
import com.create.chacha.domains.shared.member.dto.response.TokenResponseDTO;
import com.create.chacha.domains.shared.member.service.AuthValidationService;
import com.create.chacha.domains.shared.member.service.MemberLoginService;
import com.create.chacha.domains.shared.member.service.MemberSecurityService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final MemberLoginService authService;
    private final MemberSecurityService memberService;
    private final AuthValidationService authValidationService;

    // 로그인: AccessToken 바디, RefreshToken은 HttpOnly 쿠키
    @PostMapping("/login")
    public ApiResponse<TokenResponseDTO> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        log.info("로그인 요청: {}", loginRequest.getEmail());

        TokenResponseDTO tokenDTO = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        // RefreshToken 쿠키 등록
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDTO.getRefreshToken())
                .httpOnly(true)
                .secure(true)              // HTTPS 사용 시 true 권장
                .path("/")                 // 모든 경로에서 접근 가능
                .maxAge(7 * 24 * 60 * 60)  // 7일
                .sameSite("None")        // CSRF 방어(Strict), 서로 다른 포트/도메인 간 쿠키 전송(None)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // AccessToken만 반환 (RefreshToken은 쿠키로 설정했으므로 null)
        MemberEntity loginMember = MemberEntity.builder()
                .id(tokenDTO.getLogin().getId())
                .email(loginRequest.getEmail())
                .name(tokenDTO.getLogin().getName())
                .build();
        TokenResponseDTO responseDTO = new TokenResponseDTO(loginMember, tokenDTO.getAccessToken(), null);
        return new ApiResponse<>(ResponseCode.LOGIN_SUCCESS, responseDTO);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(String email, @RequestHeader("Authorization") String authHeader, HttpServletResponse response) {
        String accessToken = authHeader.substring(7);
        authService.logout(email, accessToken);

        // RefreshToken 쿠키 삭제
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "") // 값을 없앰
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // 유지 시간을 0으로 변경
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return new ApiResponse<>(ResponseCode.LOGOUT_SUCCESS, null);
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponseDTO> refresh(String email, @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        // refreshToken이 없다면 권한 없음
        if (refreshToken == null) {
            return new ApiResponse<>(ResponseCode.UNAUTHORIZED, null);
        }

        TokenResponseDTO dto = authService.refresh(email, refreshToken);
        return new ApiResponse<>(ResponseCode.REFRESH_SUCCESS, dto);
    }

    @PostMapping(value = "/join", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<MemberEntity> join(HttpSession session, @RequestBody RegisterRequestDTO registerDTO) {
        log.info("회원가입 요청: {}", registerDTO.toString());
        MemberEntity loginMember = memberService.joinUser(registerDTO);
        return new ApiResponse<>(ResponseCode.REGISTER_SUCCESS, loginMember);
    }

    /**
     * 레거시 시스템에서 호출할 토큰 검증 API
     * @param token Authorization 헤더의 Bearer 토큰
     * @return ResponseEntity<AuthValidationResponseDTO> 검증 결과 및 사용자 정보
     */
    @PostMapping("/validate")
    public ResponseEntity<AuthValidationResponseDTO> validateToken(
            @RequestHeader("Authorization") String token) {

        try {
            log.debug("토큰 검증 요청: {}", token.substring(0, Math.min(token.length(), 20)) + "...");

            // 서비스에서 토큰 검증 및 사용자 정보 조회
            AuthValidationResponseDTO response = authValidationService.validateToken(token);

            log.debug("토큰 검증 성공 - 사용자: {}, 회원ID: {}",
                    response.getUsername(), response.getMemberId());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
            return ResponseEntity.status(401).build();

        } catch (Exception e) {
            log.error("토큰 검증 중 예상치 못한 오류", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 간단한 토큰 유효성 검증 API
     * @param token Authorization 헤더의 Bearer 토큰
     * @return ResponseEntity<Void> 유효하면 200, 무효하면 401
     */
    @PostMapping("/check")
    public ResponseEntity<Void> checkToken(
            @RequestHeader("Authorization") String token) {

        try {
            boolean isValid = authValidationService.isTokenValid(token);
            return isValid ? ResponseEntity.ok().build() : ResponseEntity.status(401).build();

        } catch (Exception e) {
            log.error("토큰 체크 중 오류", e);
            return ResponseEntity.status(500).build();
        }
    }
}