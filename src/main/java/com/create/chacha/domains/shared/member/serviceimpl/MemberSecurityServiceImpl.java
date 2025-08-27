package com.create.chacha.domains.shared.member.serviceimpl;

import com.create.chacha.common.exception.DatabaseException;
import com.create.chacha.domains.shared.member.exception.*;
import com.create.chacha.config.security.SecurityUser;
import com.create.chacha.domains.shared.entity.member.MemberAddressEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.member.dto.request.RegisterRequestDTO;
import com.create.chacha.domains.shared.member.service.MemberSecurityService;
import com.create.chacha.domains.shared.repository.MemberAddressRepository;
import com.create.chacha.domains.shared.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSecurityServiceImpl implements UserDetailsService, MemberSecurityService {

    private final MemberRepository memberRepository;
    private final MemberAddressRepository memberAddressRepository;
    private final HttpSession httpSession;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MemberEntity joinUser(RegisterRequestDTO dto) {
        // 1. DTO 검증
        if (dto == null || dto.getMember() == null) {
            throw new InvalidRequestException("회원가입 정보가 없습니다.");
        }

        // 2. 이메일 중복 검사
        String email = dto.getMember().getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidRequestException("이메일이 없습니다.");
        }

        if (memberRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다: " + email);
        }

        // 3. 비밀번호 검증
        String password = dto.getMember().getPassword();
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidPasswordException("비밀번호가 없습니다.");
        }

        try {
            ModelMapper mapper = new ModelMapper();

            // 4. 엔티티 매핑
            MemberEntity member = mapper.map(dto.getMember(), MemberEntity.class);
            MemberAddressEntity address = mapper.map(dto.getMemberAddress(), MemberAddressEntity.class);

            // 5. 비밀번호 암호화
            member.setPassword(passwordEncoder.encode(password));

            // 6. 회원 저장
            MemberEntity savedMember = memberRepository.save(member);
            if (savedMember == null || savedMember.getId() == null) {
                throw new MemberSaveException("회원 정보를 저장하지 못했습니다.");
            }
            log.info("member 저장 완료: {}", savedMember.toString());
            // 7. 회원과 주소 연관관계 설정
            address.setMember(savedMember);

            // 8. 주소 저장
            MemberAddressEntity savedAddress = memberAddressRepository.save(address);
            if (savedAddress == null || savedAddress.getId() == null) {
                throw new MemberAddressNotFoundException("주소 정보를 저장하지 못했습니다.");
            }
            log.info("주소 저장 완료: {}", savedAddress.toString());

            log.info("회원가입 완료: {}", savedMember.getEmail());
            return savedMember;

        } catch (DataAccessException e) {
            log.error("데이터베이스 오류 발생: {}", e.getMessage());
            throw new DatabaseException("데이터베이스 처리 중 오류가 발생했습니다.", e);
        } catch (DuplicateEmailException | InvalidRequestException | InvalidPasswordException |
                 MemberAddressNotFoundException | MemberSaveException e) {
            // 이미 정의된 커스텀 예외들은 그대로 다시 던짐
            throw e;
        } catch (Exception e) {
            log.error("회원가입 처리 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new MemberRegistrationException("회원가입 처리 중 오류가 발생했습니다.", e);
        }
    }

    // 필수인 메서드임(로그인시 Spring이 사용함)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            throw new UsernameNotFoundException("이메일이 없습니다.");
        }

        try {
            MemberEntity member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

            return new SecurityUser(member); // UserDetail 반환

        } catch (Exception e) {
            log.error("사용자 조회 중 오류 발생: {}", e.getMessage());
            throw new UsernameNotFoundException("사용자 인증 처리 중 오류가 발생했습니다.", e);
        }
    }
}