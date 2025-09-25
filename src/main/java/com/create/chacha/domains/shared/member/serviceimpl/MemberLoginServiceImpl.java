package com.create.chacha.domains.shared.member.serviceimpl;

import com.create.chacha.common.util.JwtTokenProvider;
import com.create.chacha.domains.shared.member.dto.response.TokenResponseDTO;
import com.create.chacha.domains.shared.constants.MemberRoleEnum;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.member.exception.InvalidEmailException;
import com.create.chacha.domains.shared.member.exception.InvalidPasswordException;
import com.create.chacha.domains.shared.repository.MemberRepository;
import com.create.chacha.domains.shared.member.service.MemberLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberLoginServiceImpl implements MemberLoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public TokenResponseDTO login(String email, String password) {
        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidEmailException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidPasswordException("비밀번호 불일치");
        }

        Map<String, String> tokens = makeTokens(member);

        // Redis에 RefreshToken 저장
        redisTemplate.opsForValue().set("RT:" + email, tokens.get("refreshToken"),
                jwtTokenProvider.getExpiration(tokens.get("refreshToken")), TimeUnit.MILLISECONDS);

        return new TokenResponseDTO(member, tokens.get("accessToken"), tokens.get("refreshToken"));
    }

    @Override
    public TokenResponseDTO socialLogin(String email) {
        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자"));

        Map<String, String> tokens = makeTokens(member);

        // Redis에 RefreshToken 저장
        redisTemplate.opsForValue().set("RT:" + email, tokens.get("refreshToken"),
                jwtTokenProvider.getExpiration(tokens.get("refreshToken")), TimeUnit.MILLISECONDS);

        return new TokenResponseDTO(member, tokens.get("accessToken"), tokens.get("refreshToken"));
    }

    @Override
    public void logout(String email, String accessToken) {
        // RefreshToken 삭제
        redisTemplate.delete("RT:" + email);

        // AccessToken 블랙리스트 등록
        long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set("BL:" + accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public TokenResponseDTO refresh(String email, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get("RT:" + email);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new RuntimeException("유효하지 않은 RefreshToken 입니다.");
        }

        // DB에서 최신 사용자 정보 조회
        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자"));

        // 새로운 AccessToken 발급 (DB에서 가져온 최신 정보 사용)
        String newAccessToken = jwtTokenProvider.createAccessToken(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getMemberRole()
        );

        return new TokenResponseDTO(member, newAccessToken, refreshToken);
    }

    /**
     * 특정 사용자의 RefreshToken만 무효화
     * @param email 사용자 이메일
     */
    @Override
    public void invalidateAllTokensForUser(String email) {
        invalidateAllTokensForUser(email, null);
    }

    /**
     * 특정 사용자의 모든 토큰을 무효화 (RefreshToken + AccessToken 블랙리스트)
     * @param email 사용자 이메일
     * @param currentAccessToken 현재 사용 중인 AccessToken (선택사항)
     */
    @Override
    public void invalidateAllTokensForUser(String email, String currentAccessToken) {
        log.info("사용자의 모든 토큰 무효화 시작 - 이메일: {}", email);

        try {
            // 1. RefreshToken 삭제
            String deletedRefreshToken = redisTemplate.opsForValue().getAndDelete("RT:" + email);
            if (deletedRefreshToken != null) {
                log.debug("RefreshToken 삭제 완료 - 이메일: {}", email);
            }

            // 2. 현재 AccessToken이 있다면 블랙리스트에 추가
            if (currentAccessToken != null && !currentAccessToken.trim().isEmpty()) {
                // Bearer 접두사 제거
                String token = currentAccessToken.startsWith("Bearer ") ?
                        currentAccessToken.substring(7) : currentAccessToken;

                try {
                    // 토큰의 남은 유효시간 계산
                    long expiration = jwtTokenProvider.getExpiration(token);

                    if (expiration > 0) {
                        // 블랙리스트에 추가
                        redisTemplate.opsForValue().set("BL:" + token, "role_changed",
                                expiration, TimeUnit.MILLISECONDS);
                        log.debug("AccessToken 블랙리스트 등록 완료 - 이메일: {}", email);
                    }
                } catch (Exception e) {
                    log.warn("AccessToken 블랙리스트 등록 실패 - 이메일: {}, 오류: {}", email, e.getMessage());
                    // AccessToken 처리 실패해도 RefreshToken은 이미 삭제되었으므로 계속 진행
                }
            }

            log.info("사용자의 모든 토큰 무효화 완료 - 이메일: {}", email);

        } catch (Exception e) {
            log.error("토큰 무효화 중 오류 발생 - 이메일: {}, 오류: {}", email, e.getMessage(), e);
            throw new RuntimeException("토큰 무효화 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 특정 사용자의 토큰을 새로 발급 (기존 토큰 무효화 후)
     * @param email 사용자 이메일
     * @return TokenResponseDTO 새로 발급된 토큰 정보
     */
    @Override
    public TokenResponseDTO regenerateTokensForUser(String email) {
        log.info("사용자 토큰 재발급 요청 - 이메일: {}", email);

        try {
            // 1. 기존 토큰들 무효화
            invalidateAllTokensForUser(email);

            // 2. DB에서 최신 사용자 정보 조회
            MemberEntity member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자: " + email));


            // 3. 새로운 토큰 발급
            Map<String, String> tokens = makeTokens(member);

            // 4. 새 RefreshToken을 Redis에 저장
            redisTemplate.opsForValue().set("RT:" + email, tokens.get("refreshToken"),
                    jwtTokenProvider.getExpiration(tokens.get("refreshToken")), TimeUnit.MILLISECONDS);

            log.info("사용자 토큰 재발급 완료 - 이메일: {}", email);

            return new TokenResponseDTO(member, tokens.get("accessToken"), tokens.get("refreshToken"));

        } catch (Exception e) {
            log.error("토큰 재발급 중 오류 발생 - 이메일: {}, 오류: {}", email, e.getMessage(), e);
            throw new RuntimeException("토큰 재발급 중 오류가 발생했습니다.", e);
        }
    }

    private Map<String, String> makeTokens(MemberEntity member) {
        Map<String, String> map = new HashMap<>();
        String newAccessToken = jwtTokenProvider.createAccessToken(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getMemberRole()
        );

        String newRefreshToken = jwtTokenProvider.createRefreshToken(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getMemberRole()
        );
        map.put("accessToken", newAccessToken);
        map.put("refreshToken", newRefreshToken);
        return map;
    }
}