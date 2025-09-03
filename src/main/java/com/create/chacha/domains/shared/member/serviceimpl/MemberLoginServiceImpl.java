package com.create.chacha.domains.shared.member.serviceimpl;

import com.create.chacha.common.util.JwtTokenProvider;
import com.create.chacha.domains.shared.member.dto.response.TokenResponseDTO;
import com.create.chacha.domains.shared.constants.MemberRoleEnum;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
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
            throw new RuntimeException("비밀번호 불일치");
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

        // RefreshToken에서 사용자 정보 추출
        Long id = jwtTokenProvider.getId(refreshToken);
        String userName = jwtTokenProvider.getName(refreshToken);
        String phone = jwtTokenProvider.getPhone(refreshToken);
        MemberRoleEnum role = jwtTokenProvider.getRole(refreshToken);

        // 새로운 AccessToken 발급 (모든 사용자 정보 포함)
        String newAccessToken = jwtTokenProvider.createAccessToken(id, email, userName, phone, role);

        // MemberEntity 객체 생성 (TokenResponseDTO용)
        MemberEntity member = MemberEntity.builder()
                .id(id)
                .email(email)
                .name(userName)
                .phone(phone)
                .memberRole(role)
                .build();

        return new TokenResponseDTO(member, newAccessToken, refreshToken);
    }
}