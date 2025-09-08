package com.create.chacha.domains.shared.chat.serviceImpl;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.common.util.dto.LegacySellerDTO;
import com.create.chacha.domains.shared.chat.dto.response.ChatRoomResponseDTO;
import com.create.chacha.domains.shared.chat.exception.*;
import com.create.chacha.domains.shared.chat.service.ChatService;
import com.create.chacha.domains.shared.constants.ChatTypeEnum;
import com.create.chacha.domains.shared.entity.chat.ChatRoomEntity;
import com.create.chacha.domains.shared.entity.chat.ChatUserEntity;
import com.create.chacha.domains.shared.entity.chat.ChattingEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.repository.ChatRoomRepository;
import com.create.chacha.domains.shared.repository.ChattingRepository;
import com.create.chacha.domains.shared.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChattingRepository chattingRepository;
    private final MemberRepository memberRepository;
    private final LegacyAPIUtil legacyAPIUtil;

    private static final AtomicLong groupRoomCounter = new AtomicLong(1);

    // 1대1 채팅방 생성 또는 찾기
    @Override
    public ChatRoomEntity createOrFindPersonalChatRoom(Long buyerId, String storeUrl) {
        MemberEntity buyer = memberRepository.findById(buyerId)
                .orElseThrow(() -> new ChatMemberNotFoundException(buyerId));

        LegacySellerDTO legacySeller;
        try {
            legacySeller = legacyAPIUtil.getLegacySellerData(storeUrl);
        } catch (Exception e) {
            log.error("스토어 정보 조회 실패: {}", storeUrl, e);
            throw new ChatInvalidRequestException("스토어 정보를 찾을 수 없습니다: " + storeUrl);
        }

        MemberEntity seller = memberRepository.findById(legacySeller.getMemberId().longValue())
                .orElseThrow(() -> new ChatMemberNotFoundException(legacySeller.getMemberId().longValue()));

        // AESConverter로 자동 복호화된 이름 사용
        String buyerName = buyer.getName();
        String sellerName = seller.getName();

        String chatroomId = "personal_" + buyerName + "_" + sellerName;

        Optional<ChatRoomEntity> existingRoom = chatRoomRepository.findByChatroomId(chatroomId);
        if (existingRoom.isPresent()) {
            log.info("기존 채팅방 반환: {}", chatroomId);
            return existingRoom.get();
        }

        List<ChatUserEntity> users = Arrays.asList(
                new ChatUserEntity(buyerId, buyerName),
                new ChatUserEntity(legacySeller.getMemberId().longValue(), sellerName)
        );

        ChatRoomEntity chatRoom = new ChatRoomEntity(chatroomId, users);
        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom);
        log.info("새 채팅방 생성: {}", chatroomId);
        return savedChatRoom;
    }

    // 단체 채팅방 생성
    @Override
    public ChatRoomEntity createGroupChatRoom(List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            throw new ChatInvalidRequestException("참여할 회원 목록이 비어있습니다.");
        }

        if (memberIds.size() < 2) {
            throw new ChatInvalidRequestException("단체 채팅방은 최소 2명 이상이어야 합니다.");
        }

        List<ChatUserEntity> users = memberIds.stream()
                .map(memberId -> {
                    MemberEntity member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new ChatMemberNotFoundException(memberId));
                    return new ChatUserEntity(memberId, member.getName());
                })
                .collect(Collectors.toList());

        String chatroomId = "group_room_" + groupRoomCounter.getAndIncrement();
        ChatRoomEntity chatRoom = new ChatRoomEntity(chatroomId, users);
        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom);
        log.info("단체 채팅방 생성: {}, 참여자 수: {}", chatroomId, users.size());
        return savedChatRoom;
    }

    // 관리자 채팅방 생성 또는 찾기
    @Override
    public ChatRoomEntity createOrFindAdminChatRoom(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ChatMemberNotFoundException(memberId));

        // AESConverter로 자동 복호화된 이름 사용
        String memberName = member.getName();
        String chatroomId = "admin_" + memberName;

        Optional<ChatRoomEntity> existingRoom = chatRoomRepository.findByChatroomId(chatroomId);
        if (existingRoom.isPresent()) {
            log.info("기존 관리자 채팅방 반환: {}", chatroomId);
            return existingRoom.get();
        }

        List<ChatUserEntity> users = Arrays.asList(
                new ChatUserEntity(memberId, memberName),
                new ChatUserEntity(null, "관리자") // 관리자는 별도 ID 없음
        );

        ChatRoomEntity chatRoom = new ChatRoomEntity(chatroomId, users);
        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom);
        log.info("새 관리자 채팅방 생성: {}", chatroomId);
        return savedChatRoom;
    }

    // 메시지 저장 (WebSocket을 통해 호출)
    @Override
    public ChattingEntity saveMessage(ChattingEntity chatting) {
        if (chatting == null) {
            throw new ChatInvalidRequestException("메시지 정보가 없습니다.");
        }

        if (chatting.getMessage() == null || chatting.getMessage().trim().isEmpty()) {
            throw new ChatInvalidRequestException("메시지 내용이 비어있습니다.");
        }

        if (chatting.getChatroomId() == null || chatting.getChatroomId().trim().isEmpty()) {
            throw new ChatInvalidRequestException("채팅방 ID가 없습니다.");
        }

        // 채팅방 존재 여부 확인
        chatRoomRepository.findByChatroomId(chatting.getChatroomId())
                .orElseThrow(() -> new ChatRoomNotFoundException(chatting.getChatroomId()));

        // 발신자 정보 설정 (AESConverter로 자동 복호화됨)
        if (chatting.getSenderId() != null) {
            MemberEntity sender = memberRepository.findById(chatting.getSenderId())
                    .orElseThrow(() -> new ChatMemberNotFoundException(chatting.getSenderId()));
            // AESConverter가 자동으로 복호화한 이름 사용
            chatting.setSenderName(sender.getName());
        }

        try {
            ChattingEntity savedMessage = chattingRepository.save(chatting);
            log.debug("메시지 저장 완료 - 채팅방: {}, 발신자: {}",
                    chatting.getChatroomId(), chatting.getSenderName());
            return savedMessage;
        } catch (Exception e) {
            log.error("메시지 저장 실패", e);
            throw new ChatMessageSendException("메시지 저장에 실패했습니다: " + e.getMessage());
        }
    }

    // 관리자 메시지 저장
    @Override
    public ChattingEntity saveAdminMessage(String chatroomId, Long receiverId, String message) {
        if (chatroomId == null || chatroomId.trim().isEmpty()) {
            throw new ChatInvalidRequestException("채팅방 ID가 없습니다.");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new ChatInvalidRequestException("메시지 내용이 비어있습니다.");
        }

        if (receiverId == null) {
            throw new ChatInvalidRequestException("수신자 ID가 없습니다.");
        }

        // 채팅방 존재 여부 확인
        chatRoomRepository.findByChatroomId(chatroomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(chatroomId));

        // 수신자 존재 여부 확인
        memberRepository.findById(receiverId)
                .orElseThrow(() -> new ChatMemberNotFoundException(receiverId));

        ChattingEntity chatting = ChattingEntity.builder()
                .chatroomId(chatroomId)
                .senderId(null) // 관리자는 senderId가 null
                .receiverId(receiverId)
                .senderName("관리자")
                .message(message.trim())
                .type(ChatTypeEnum.ADMIN)
                .sendAt(LocalDateTime.now())
                .build();

        try {
            ChattingEntity savedMessage = chattingRepository.save(chatting);
            log.info("관리자 메시지 저장 완료 - 채팅방: {}, 수신자: {}", chatroomId, receiverId);
            return savedMessage;
        } catch (Exception e) {
            log.error("관리자 메시지 저장 실패", e);
            throw new ChatMessageSendException("관리자 메시지 저장에 실패했습니다: " + e.getMessage());
        }
    }

    // 채팅방 메시지 조회
    @Override
    public List<ChattingEntity> getChatHistory(String chatroomId) {
        if (chatroomId == null || chatroomId.trim().isEmpty()) {
            throw new ChatInvalidRequestException("채팅방 ID가 없습니다.");
        }

        // 채팅방 존재 여부 확인
        chatRoomRepository.findByChatroomId(chatroomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(chatroomId));

        return chattingRepository.findByChatroomIdOrderBySendAtAsc(chatroomId);
    }

    // 사용자 채팅방 목록 조회 (ChatRoomResponseDTO 반환)
    @Override
    public List<ChatRoomResponseDTO> getUserChatRooms(Long memberId) {
        if (memberId == null) {
            throw new ChatInvalidRequestException("회원 ID가 없습니다.");
        }

        // 회원 존재 여부 확인
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ChatMemberNotFoundException(memberId));

        List<ChatRoomEntity> chatRooms = chatRoomRepository.findByUsers_MemberId(memberId);

        return chatRooms.stream().map(room -> {
            // 마지막 메시지 조회
            List<ChattingEntity> lastMessages = chattingRepository
                    .findTop1ByChatroomIdOrderBySendAtDesc(room.getChatroomId());

            // 현재 사용자의 lastReadAt 시간
            ChatUserEntity currentUser = room.getUsers().stream()
                    .filter(user -> user.getMemberId() != null && user.getMemberId().equals(memberId))
                    .findFirst().orElse(null);

            long unreadCount = 0;
            String lastMessage = "";
            LocalDateTime lastMessageTime = null;

            if (!lastMessages.isEmpty()) {
                ChattingEntity lastMsg = lastMessages.get(0);
                lastMessage = lastMsg.getMessage();
                lastMessageTime = lastMsg.getSendAt();

                // 안읽은 메시지 개수 계산
                if (currentUser != null && currentUser.getLastReadAt() != null) {
                    unreadCount = chattingRepository
                            .countByChatroomIdAndSendAtAfter(room.getChatroomId(),
                                    currentUser.getLastReadAt());
                } else {
                    // lastReadAt이 없으면 모든 메시지가 안읽음
                    unreadCount = chattingRepository.countByChatroomId(room.getChatroomId());
                }
            }

            // 참여자 이름 목록
            List<String> memberNames = room.getUsers().stream()
                    .map(ChatUserEntity::getMemberName)
                    .collect(Collectors.toList());

            return ChatRoomResponseDTO.builder()
                    .chatroomId(room.getChatroomId())
                    .memberNames(memberNames)
                    .lastMessage(lastMessage)
                    .lastMessageTime(lastMessageTime)
                    .unreadCount(unreadCount)
                    .build();
        }).collect(Collectors.toList());
    }

    // 사용자 채팅방 목록 조회 (Entity 반환 - 내부 사용)
    @Override
    public List<ChatRoomEntity> getUserChatRoomEntities(Long memberId) {
        if (memberId == null) {
            throw new ChatInvalidRequestException("회원 ID가 없습니다.");
        }

        // 회원 존재 여부 확인
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ChatMemberNotFoundException(memberId));

        return chatRoomRepository.findByUsers_MemberId(memberId);
    }

    // 읽음 처리
    @Override
    public void updateLastReadAt(String chatroomId, Long memberId) {
        if (chatroomId == null || chatroomId.trim().isEmpty()) {
            throw new ChatInvalidRequestException("채팅방 ID가 없습니다.");
        }

        if (memberId == null) {
            throw new ChatInvalidRequestException("회원 ID가 없습니다.");
        }

        ChatRoomEntity chatRoom = chatRoomRepository.findByChatroomId(chatroomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(chatroomId));

        // 회원이 해당 채팅방에 참여하고 있는지 확인
        boolean isMember = chatRoom.getUsers().stream()
                .anyMatch(user -> user.getMemberId() != null && user.getMemberId().equals(memberId));

        if (!isMember) {
            throw new ChatAccessDeniedException("해당 채팅방에 접근할 권한이 없습니다.");
        }

        // lastReadAt 업데이트
        chatRoom.getUsers().stream()
                .filter(user -> user.getMemberId() != null && user.getMemberId().equals(memberId))
                .forEach(user -> user.setLastReadAt(LocalDateTime.now()));

        try {
            chatRoomRepository.save(chatRoom);
            log.debug("읽음 처리 완료 - 채팅방: {}, 회원: {}", chatroomId, memberId);
        } catch (Exception e) {
            log.error("읽음 처리 실패", e);
            throw new ChatMessageSendException("읽음 처리에 실패했습니다: " + e.getMessage());
        }
    }

    // 회원 정보 조회
    @Override
    public MemberEntity getMemberById(Long memberId) {
        if (memberId == null) {
            throw new ChatInvalidRequestException("회원 ID가 없습니다.");
        }

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ChatMemberNotFoundException(memberId));
    }

    // 이메일로 회원 조회
    @Override
    public MemberEntity getMemberByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ChatInvalidRequestException("이메일이 없습니다.");
        }

        return memberRepository.findByEmail(email.trim())
                .orElseThrow(() -> new ChatMemberNotFoundException(email));
    }

    // 회원 이름으로 채팅방 검색
    @Override
    public List<ChatRoomEntity> searchChatRoomsByMemberName(String memberName) {
        if (memberName == null || memberName.trim().isEmpty()) {
            throw new ChatInvalidRequestException("검색할 회원 이름을 입력해주세요.");
        }

        String searchName = memberName.trim();
        return chatRoomRepository.findAll().stream()
                .filter(room -> room.getUsers().stream()
                        .anyMatch(user -> user.getMemberName() != null &&
                                user.getMemberName().contains(searchName)))
                .collect(Collectors.toList());
    }

    // 회원 상세 정보 조회 (개인정보 마스킹 처리)
    @Override
    public MemberEntity getMemberInfoForChat(Long memberId) {
        if (memberId == null) {
            throw new ChatInvalidRequestException("회원 ID가 없습니다.");
        }

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ChatMemberNotFoundException(memberId));

        // 채팅에서는 민감한 정보 노출 방지
        // phone, registrationNumber는 마스킹 처리하거나 별도 DTO 사용 권장
        return member;
    }
}