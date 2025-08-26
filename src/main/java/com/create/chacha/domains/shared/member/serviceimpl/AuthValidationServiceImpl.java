package com.create.chacha.domains.shared.member.serviceimpl;

import com.create.chacha.common.util.JwtTokenProvider;
import com.create.chacha.config.security.SecurityUser;
import com.create.chacha.domains.shared.member.dto.response.AuthValidationResponseDTO;
import com.create.chacha.domains.shared.member.service.AuthValidationService;
import com.create.chacha.domains.shared.member.service.MemberSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthValidationServiceImpl implements AuthValidationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberSecurityService memberSecurityService;

    /**
     * JWT 토큰 검증 및 사용자 정보 반환
     * @param token Bearer 토큰 (Bearer 접두사 포함)
     * @return AuthValidationResponseDTO 사용자 정보 응답 객체
     * @throws RuntimeException 토큰이 유효하지 않을 경우
     */
    @Override
    public AuthValidationResponseDTO validateToken(String token) {
        try {
            // Bearer 접두사 제거
            String jwt = token.replace("Bearer ", "");

            // 토큰 유효성 검증
            if (!jwtTokenProvider.validateToken(jwt)) {
                throw new RuntimeException("유효하지 않은 토큰입니다.");
            }

            // 토큰에서 이메일 추출
            String email = jwtTokenProvider.getEmail(jwt);
            log.debug("토큰에서 추출된 이메일: {}", email);

            // 사용자 정보 조회
            UserDetails userDetails = memberSecurityService.loadUserByUsername(email);
            SecurityUser securityUser = (SecurityUser) userDetails;

            // 응답 객체 생성
            return AuthValidationResponseDTO.builder()
                    .memberId(securityUser.getMemberId())
                    .username(email)
                    .memberRole(securityUser.getMemberRole())
                    .build();

        } catch (Exception e) {
            log.error("토큰 검증 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("토큰 검증 실패: " + e.getMessage(), e);
        }
    }

    /**
     * JWT 토큰 유효성만 간단히 검증
     * @param token Bearer 토큰 (Bearer 접두사 포함)
     * @return boolean 토큰 유효 여부
     */
    @Override
    public boolean isTokenValid(String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            return jwtTokenProvider.validateToken(jwt);
        } catch (Exception e) {
            log.debug("토큰 유효성 검증 실패: {}", e.getMessage());
            return false;
        }
    }
}