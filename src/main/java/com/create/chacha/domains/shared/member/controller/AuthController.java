package com.create.chacha.domains.shared.member.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.member.dto.request.LoginRequestDTO;
import com.create.chacha.domains.shared.member.dto.request.RegisterRequestDTO;
import com.create.chacha.domains.shared.member.dto.response.TokenResponseDTO;
import com.create.chacha.domains.shared.member.service.MemberLoginService;
import com.create.chacha.domains.shared.member.service.MemberSecurityService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class AuthController {

    private final MemberLoginService authService;
    private final MemberSecurityService memberService;

    // 로그인: AccessToken 바디, RefreshToken은 HttpOnly 쿠키
    @PostMapping("/auth/login")
    public ApiResponse<TokenResponseDTO> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        log.info("로그인 요청: {}", loginRequest.getEmail());

        TokenResponseDTO tokenDTO = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        // RefreshToken 쿠키 등록
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDTO.getRefreshToken())
                .httpOnly(true)
                .secure(true)              // HTTPS 사용 시 true 권장
                .path("/")                 // 모든 경로에서 접근 가능
                .maxAge(7 * 24 * 60 * 60)  // 7일
                .sameSite("Strict")        // CSRF 방어
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // AccessToken만 반환 (RefreshToken은 쿠키로 설정했으므로 null)
        TokenResponseDTO responseDTO = new TokenResponseDTO(loginRequest.getEmail(), tokenDTO.getAccessToken(), null);
        return new ApiResponse<>(ResponseCode.LOGIN_SUCCESS, responseDTO);
    }

    @PostMapping("/auth/logout")
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

    @PostMapping("/auth/refresh")
    public ApiResponse<TokenResponseDTO> refresh(String email, @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        // refreshToken이 없다면 권한 없음
        if (refreshToken == null) {
            return new ApiResponse<>(ResponseCode.UNAUTHORIZED, null);
        }

        TokenResponseDTO dto = authService.refresh(email, refreshToken);
        return new ApiResponse<>(ResponseCode.REFRESH_SUCCESS, dto);
    }

    @PostMapping(value = "/auth/join", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<MemberEntity> join(HttpSession session, @RequestBody RegisterRequestDTO registerDTO) {
        log.info("회원가입 요청: {}", registerDTO.toString());
        MemberEntity loginMember = memberService.joinUser(registerDTO);
        return new ApiResponse<>(ResponseCode.REGISTER_SUCCESS, loginMember);
    }
}