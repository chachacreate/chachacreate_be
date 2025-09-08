package com.create.chacha.domains.shared.chat.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AdminMessageRequestDTO {
    private String chatroomId;
    private Long receiverId;
    private String message;
}
