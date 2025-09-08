package com.create.chacha.domains.shared.chat.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatRoomDetailResponseDTO {
    private String chatroomId;
    private List<MemberChatInfoResponseDTO> members;
    private String chatType; // PERSONAL, GROUP, ADMIN
    private LocalDateTime createdAt;
    private long totalMessages;
    private LocalDateTime lastActivity;
}
