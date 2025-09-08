package com.create.chacha.domains.shared.chat.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatNotificationDTO {
    private String type; // NEW_MESSAGE, USER_JOINED, USER_LEFT
    private String chatroomId;
    private String senderName;
    private String message;
    private LocalDateTime timestamp;
}
