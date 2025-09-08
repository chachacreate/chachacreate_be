package com.create.chacha.domains.shared.chat.dto.request;

import com.create.chacha.domains.shared.constants.ChatTypeEnum;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatMessageRequestDTO {
    private Long senderId;
    private Long receiverId;
    private String message;
    private ChatTypeEnum type;
}
