package com.create.chacha.domains.shared.member.serviceimpl;

import com.create.chacha.common.util.JwtTokenProvider;
import com.create.chacha.domains.shared.member.dto.response.TokenResponseDTO;
import com.create.chacha.domains.shared.constants.MemberRoleEnum;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.member.exception.InvalidPasswordException;
import com.create.chacha.domains.shared.repository.MemberRepository;
import com.create.chacha.domains.shared.member.service.MemberLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자"));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidPasswordException("비밀번호 불일치");
        }

        String accessToken = jwtTokenProvider.createAccessToken(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getMemberRole()
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getMemberRole()
        );

        // Redis에 RefreshToken 저장
        redisTemplate.opsForValue().set("RT:" + email, refreshToken,
                jwtTokenProvider.getExpiration(refreshToken), TimeUnit.MILLISECONDS);

        return new TokenResponseDTO(member, accessToken, refreshToken);
    }

    @Override
    public TokenResponseDTO socialLogin(String email) {
        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자"));

        String accessToken = jwtTokenProvider.createAccessToken(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getMemberRole()
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getMemberRole()
        );

        // Redis에 RefreshToken 저장
        redisTemplate.opsForValue().set("RT:" + email, refreshToken,
                jwtTokenProvider.getExpiration(refreshToken), TimeUnit.MILLISECONDS);

        return new TokenResponseDTO(member, accessToken, refreshToken);
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
}