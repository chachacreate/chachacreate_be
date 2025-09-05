package com.create.chacha.domains.buyer.areas.mypage.service.serviceImpl;

import com.create.chacha.domains.buyer.areas.mypage.service.ChangePasswordService;
import com.create.chacha.domains.buyer.exception.mypage.PasswordValidationException;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChangePasswordServiceImpl implements ChangePasswordService {

    private static final String ALLOWED_SPECIAL = "!\"#$%&'()*+,-./:;<=>?@[₩]^_`{|}~";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void changePasswordFor(Long memberId, String currentPwd,
                                                       String newPwd, String newConfirmPwd) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPwd, member.getPassword())) {
            throw new BadCredentialsException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호와 확인 일치 여부
        if (!newPwd.equals(newConfirmPwd)) {
            throw new IllegalArgumentException("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        // 비밀번호 정책 검증
        validatePassword(newPwd);

        // 비밀번호 변경
        member.setPassword(passwordEncoder.encode(newPwd));

        // 감사 로그
        log.info("Member {} changed password at {}", memberId, LocalDateTime.now());
    }

    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new PasswordValidationException("비밀번호는 최소 8자 이상이어야 합니다.");
        }
        if (!password.matches(".*[A-Za-z].*")) {
            throw new PasswordValidationException("비밀번호에는 최소 1개의 영어가 포함되어야 합니다.");
        }
        if (!password.matches(".*\\d.*")) {
            throw new PasswordValidationException("비밀번호에는 최소 1개의 숫자가 포함되어야 합니다.");
        }
        String specialPattern = ".*[" + Pattern.quote(ALLOWED_SPECIAL) + "].*";
        if (!password.matches(specialPattern)) {
            throw new PasswordValidationException("비밀번호에는 최소 1개의 특수문자가 포함되어야 합니다.");
        }
        String notAllowedPattern = "[^A-Za-z\\d" + Pattern.quote(ALLOWED_SPECIAL) + "]";
        if (password.matches(".*" + notAllowedPattern + ".*")) {
            throw new PasswordValidationException("비밀번호에 허용되지 않은 문자가 포함되어 있습니다.");
        }
    }
}
