package com.create.chacha.domains.shared.member.service;

import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.member.dto.request.RegisterRequestDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public interface MemberSecurityService {

    /**
     * 회원가입 처리
     * @param registerRequestDTO 회원가입 요청 DTO
     * @return MemberEntity 저장된 회원 정보
     */
    public MemberEntity joinUser(RegisterRequestDTO registerRequestDTO);

    /**
     * 사용자 인증 정보 조회 (Spring Security UserDetailsService 메서드)
     * @param email 사용자 이메일
     * @return UserDetails 사용자 인증 정보
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
