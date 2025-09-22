package com.create.chacha.domains.shared.repository;

import com.create.chacha.domains.shared.entity.chat.ChattingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChattingRepository extends MongoRepository<ChattingEntity, String> {
    List<ChattingEntity> findByChatroomIdOrderBySendAtAsc(String chatroomId);
    List<ChattingEntity> findTop1ByChatroomIdOrderBySendAtDesc(String chatroomId);
    long countByChatroomIdAndSendAtAfter(String chatroomId, LocalDateTime after);
    long countByChatroomId(String chatroomId);
}
