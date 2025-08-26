package com.create.chacha.domains.shared.member.service;

import com.create.chacha.common.util.JwtTokenProvider;
import com.create.chacha.domains.shared.member.dto.response.AuthValidationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


public interface AuthValidationService {
    /**
     * JWT 토큰 검증 및 사용자 정보 반환
     * @param token Bearer 토큰 (Bearer 접두사 포함)
     * @return AuthValidationResponseDTO 사용자 정보 응답 객체
     * @throws RuntimeException 토큰이 유효하지 않을 경우
     */
    AuthValidationResponseDTO validateToken(String token);

    /**
     * JWT 토큰 유효성만 간단히 검증
     * @param token Bearer 토큰 (Bearer 접두사 포함)
     * @return boolean 토큰 유효 여부
     */
    boolean isTokenValid(String token);
}
