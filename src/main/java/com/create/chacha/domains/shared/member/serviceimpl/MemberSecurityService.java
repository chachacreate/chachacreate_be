package com.create.chacha.domains.shared.member.serviceimpl;

import com.create.chacha.config.security.SecurityUser;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {

    MemberRepository memberRepository;
    HttpSession httpSession;
    PasswordEncoder passwordEncoder;

    //***사용자등록로직 추가하기
    public MemberEntity joinUser(MemberEntity member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }

    //필수인 메서드임(로그인시 Spring이 사용함)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<MemberEntity> memberOp = memberRepository.findByEmail(email);
        MemberEntity member = memberOp.orElse(null);

        //MemberEntity ==> SecurityUser 변경
        UserDetails user = memberOp.filter(m->m!= null).map(m->new SecurityUser(member)).get();
        httpSession.setAttribute("loginMember", member);
        return user;
    }
}
