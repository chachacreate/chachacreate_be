package com.create.chacha.domains.shared.member.controller;

import com.create.chacha.domains.shared.member.dto.response.TokenResponseDTO;
import com.create.chacha.domains.shared.member.service.MemberLoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final MemberLoginService authService;

    // 로그인: AccessToken 바디, RefreshToken은 HttpOnly 쿠키
    @PostMapping("/auth/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
        TokenResponseDTO tokenDTO = authService.login(email, password);

        // RefreshToken 쿠키 등록
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDTO.getRefreshToken())
                .httpOnly(true)
                .secure(true)              // HTTPS 사용 시 true 권장
                .path("/")                 // 모든 경로에서 접근 가능
                .maxAge(7 * 24 * 60 * 60)  // 7일
                .sameSite("Strict")        // CSRF 방어
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        
        // AccessToken 바디 응답으로 전송
        return ResponseEntity.ok(new TokenResponseDTO(email, tokenDTO.getAccessToken(), null));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@RequestParam String email, @RequestHeader("Authorization") String authHeader, HttpServletResponse response) {
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

        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenResponseDTO> refresh(@RequestParam String email, @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        // refreshToken이 없다면 권한 없음(401)
        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        TokenResponseDTO dto = authService.refresh(email, refreshToken);
        return ResponseEntity.ok(dto);
    }
}
