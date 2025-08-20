package com.create.chacha.domains.shared.member.service;

import com.create.chacha.domains.shared.entity.member.MemberEntity;
import org.springframework.security.core.userdetails.UserDetails;


public interface MemberService {

    public MemberEntity joinUser(MemberEntity member);

    public UserDetails loadUserByUsername(String username);
}
