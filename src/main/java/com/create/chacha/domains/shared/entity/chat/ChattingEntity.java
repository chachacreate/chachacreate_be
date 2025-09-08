package com.create.chacha.domains.shared.entity.chat;

import com.create.chacha.domains.shared.constants.ChatTypeEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Document(collection = "chattings")
public class ChattingEntity {
    @Id
    private String id; // MongoDB ObjectId
    private String chatroomId;
    private Long senderId; // MemberEntity의 ID 사용
    private Long receiverId; // MemberEntity의 ID 사용
    private String senderName;
    private String message;
    private ChatTypeEnum type;
    private LocalDateTime sendAt;

    public ChattingEntity() {
        this.sendAt = LocalDateTime.now();
    }
}

