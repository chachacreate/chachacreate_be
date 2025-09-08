package com.create.chacha.domains.shared.entity.chat;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ChatUserEntity {
    private Long memberId; // MemberEntity의 ID 사용
    private String memberName;
    private LocalDateTime lastReadAt;

    public ChatUserEntity(Long memberId, String memberName) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.lastReadAt = LocalDateTime.now();
    }
}
