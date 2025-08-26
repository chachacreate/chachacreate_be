package com.create.chacha.domains.shared.member.dto.response;

import com.create.chacha.domains.shared.constants.MemberRoleEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthValidationResponseDTO {
    /**
     * 회원 ID
     */
    private Long memberId;

    /**
     * 사용자명 (로그인 ID)
     */
    private String username;

    /**
     * 회원 권한
     */
    private MemberRoleEnum memberRole;
}
