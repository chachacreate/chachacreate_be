package com.create.chacha.domains.shared.repository;

import com.create.chacha.domains.shared.entity.chat.ChatRoomEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoomEntity, String> {
    Optional<ChatRoomEntity> findByChatroomId(String chatroomId);
    List<ChatRoomEntity> findByUsers_MemberId(Long memberId);
}