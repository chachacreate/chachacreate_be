package com.create.chacha.domains.shared.member.dto.request;

import com.create.chacha.domains.shared.constants.MemberRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRoleUpdateRequestDTO {

    private Long memberId;
    private MemberRoleEnum memberRole;
}
