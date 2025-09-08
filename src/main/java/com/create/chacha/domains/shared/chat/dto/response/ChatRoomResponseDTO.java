package com.create.chacha.domains.shared.chat.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatRoomResponseDTO {
    private String chatroomId;
    private List<String> memberNames;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private long unreadCount;
}
