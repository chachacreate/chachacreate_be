package com.create.chacha.domains.shared.chat.dto.response;

import com.create.chacha.domains.shared.constants.MemberRoleEnum;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MemberChatInfoResponseDTO {
    private Long id;
    private String name; // AESConverter로 복호화된 이름
    private String email;
    private MemberRoleEnum memberRole;
    // phone, registrationNumber 등 민감정보는 제외
}
