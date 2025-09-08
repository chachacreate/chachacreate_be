package com.create.chacha.domains.shared.entity.chat;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Document(collection = "chatrooms")
public class ChatRoomEntity {
    @Id
    private String id; // MongoDB ObjectId
    private String chatroomId; // 채팅방 참조 ID
    private List<ChatUserEntity> users;
    private LocalDateTime createdAt;

    public ChatRoomEntity(String chatroomId, List<ChatUserEntity> users) {
        this.chatroomId = chatroomId;
        this.users = users;
        this.createdAt = LocalDateTime.now();
    }
}
