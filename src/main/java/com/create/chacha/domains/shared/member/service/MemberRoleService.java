package com.create.chacha.domains.shared.member.service;

import com.create.chacha.domains.shared.constants.MemberRoleEnum;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.member.dto.response.TokenResponseDTO;

public interface MemberRoleService {
    MemberEntity updateMemberRole(Long memberId, MemberRoleEnum memberRole);
    MemberEntity getMemberRole(Long memberId);
    TokenResponseDTO updateMemberRoleWithToken(Long memberId, MemberRoleEnum memberRole);
}
