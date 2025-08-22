package com.create.chacha.domains.shared.member.service;

import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.member.dto.request.RegisterRequestDTO;
import org.springframework.security.core.userdetails.UserDetails;


public interface MemberSecurityService {

    public MemberEntity joinUser(RegisterRequestDTO registerRequestDTO);
}
