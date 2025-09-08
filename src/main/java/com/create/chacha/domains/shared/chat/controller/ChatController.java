package com.create.chacha.domains.shared.chat.controller;


import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.common.util.Base64Util;
import com.create.chacha.config.chat.ChatWebSocketHandler;
import com.create.chacha.domains.shared.chat.dto.response.ChatRoomResponseDTO;
import com.create.chacha.domains.shared.chat.dto.response.MemberChatInfoResponseDTO;
import com.create.chacha.domains.shared.chat.exception.ChatInvalidRequestException;
import com.create.chacha.domains.shared.chat.exception.ChatMessageSendException;
import com.create.chacha.domains.shared.chat.service.ChatService;
import com.create.chacha.domains.shared.entity.chat.ChatRoomEntity;
import com.create.chacha.domains.shared.entity.chat.ChattingEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatWebSocketHandler chatWebSocketHandler;

    // 1대1 채팅방 생성
    @PostMapping("/personal/{storeUrl}")
    public ApiResponse<ChatRoomEntity> createPersonalChat(@RequestParam Long buyerId,
                                                          @PathVariable String storeUrl) {
        ChatRoomEntity chatRoom = chatService.createOrFindPersonalChatRoom(buyerId, storeUrl);
        return new ApiResponse<>(ResponseCode.CHAT_ROOM_CREATED, chatRoom);
    }

    // 단체 채팅방 생성
    @PostMapping("/group")
    public ApiResponse<ChatRoomEntity> createGroupChat(@RequestBody List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            throw new ChatInvalidRequestException("참여할 회원 목록이 비어있습니다.");
        }
        ChatRoomEntity chatRoom = chatService.createGroupChatRoom(memberIds);
        return new ApiResponse<>(ResponseCode.CHAT_ROOM_CREATED, chatRoom);
    }

    // 관리자 채팅방 생성
    @PostMapping("/admin")
    public ApiResponse<ChatRoomEntity> createAdminChat(@RequestParam Long memberId) {
        ChatRoomEntity chatRoom = chatService.createOrFindAdminChatRoom(memberId);
        return new ApiResponse<>(ResponseCode.CHAT_ROOM_CREATED, chatRoom);
    }

    // 사용자 채팅방 목록
    @GetMapping("/rooms/{memberId}")
    public ApiResponse<List<ChatRoomResponseDTO>> getUserChatRooms(@PathVariable Long memberId) {
        List<ChatRoomResponseDTO> chatRooms = chatService.getUserChatRooms(memberId);
        if (chatRooms.isEmpty()) {
            return new ApiResponse<>(ResponseCode.CHAT_ROOMS_NOT_FOUND, chatRooms);
        }
        return new ApiResponse<>(ResponseCode.CHAT_ROOMS_FOUND, chatRooms);
    }

    // 채팅 히스토리 (Base64 디코딩 적용)
    @GetMapping("/history/{chatroomId}")
    public ApiResponse<List<ChattingEntity>> getChatHistory(@PathVariable String chatroomId) {
        // Base64 디코딩 시도
        String decodedChatroomId = Base64Util.decodeChatroomId(chatroomId);

        log.debug("채팅 히스토리 조회 - 원본: {}, 디코딩: {}", chatroomId, decodedChatroomId);

        List<ChattingEntity> history = chatService.getChatHistory(decodedChatroomId);
        if (history.isEmpty()) {
            return new ApiResponse<>(ResponseCode.CHAT_HISTORY_NOT_FOUND, history);
        }
        return new ApiResponse<>(ResponseCode.CHAT_HISTORY_FOUND, history);
    }

    // 읽음 처리 (Base64 디코딩 적용)
    @PutMapping("/read")
    public ApiResponse<String> markAsRead(@RequestParam String chatroomId,
                                          @RequestParam Long memberId) {
        // Base64 디코딩
        String decodedChatroomId = Base64Util.decodeChatroomId(chatroomId);

        log.info("읽음 처리 - 원본: {}, 디코딩: {}", chatroomId, decodedChatroomId);

        chatService.updateLastReadAt(decodedChatroomId, memberId);
        return new ApiResponse<>(ResponseCode.CHAT_READ_STATUS_UPDATED, "읽음 처리 완료");
    }

    // 관리자 메시지 전송 (Base64 디코딩 적용)
    @PostMapping("/admin/send")
    public ApiResponse<String> sendAdminMessage(@RequestParam String chatroomId,
                                                @RequestParam Long receiverId,
                                                @RequestParam String message) {
        try {
            // Base64 디코딩
            String decodedChatroomId = Base64Util.decodeChatroomId(chatroomId);

            log.info("관리자 메시지 전송 - 원본: {}, 디코딩: {}", chatroomId, decodedChatroomId);

            chatWebSocketHandler.sendAdminMessage(decodedChatroomId, receiverId, message);
            return new ApiResponse<>(ResponseCode.CHAT_MESSAGE_SENT, "관리자 메시지 전송 완료");
        } catch (Exception e) {
            log.error("관리자 메시지 전송 실패", e);
            throw new ChatMessageSendException("관리자 메시지 전송에 실패했습니다: " + e.getMessage());
        }
    }

    // 회원 정보 조회
    @GetMapping("/member/{memberId}")
    public ApiResponse<MemberChatInfoResponseDTO> getMember(@PathVariable Long memberId) {
        MemberEntity member = chatService.getMemberById(memberId);
        MemberChatInfoResponseDTO memberInfo = MemberChatInfoResponseDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .memberRole(member.getMemberRole())
                .build();
        return new ApiResponse<>(ResponseCode.OK, memberInfo);
    }

    // 이메일로 회원 조회
    @GetMapping("/member/email/{email}")
    public ApiResponse<MemberChatInfoResponseDTO> getMemberByEmail(@PathVariable String email) {
        MemberEntity member = chatService.getMemberByEmail(email);
        MemberChatInfoResponseDTO memberInfo = MemberChatInfoResponseDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .memberRole(member.getMemberRole())
                .build();
        return new ApiResponse<>(ResponseCode.OK, memberInfo);
    }

    // 채팅방 검색
    @GetMapping("/search")
    public ApiResponse<List<ChatRoomEntity>> searchChatRooms(@RequestParam String memberName) {
        if (memberName == null || memberName.trim().isEmpty()) {
            throw new ChatInvalidRequestException("검색할 회원 이름을 입력해주세요.");
        }

        List<ChatRoomEntity> chatRooms = chatService.searchChatRoomsByMemberName(memberName);
        if (chatRooms.isEmpty()) {
            return new ApiResponse<>(ResponseCode.CHAT_SEARCH_NO_RESULTS, chatRooms);
        }
        return new ApiResponse<>(ResponseCode.CHAT_SEARCH_SUCCESS, chatRooms);
    }

    // 사용자에게 알림 전송
    @PostMapping("/notification")
    public ApiResponse<String> sendNotification(@RequestParam Long userId,
                                                @RequestParam String notification) {
        try {
            chatWebSocketHandler.sendNotificationToUser(userId, notification);
            return new ApiResponse<>(ResponseCode.CHAT_NOTIFICATION_SENT, "알림 전송 완료");
        } catch (Exception e) {
            throw new ChatMessageSendException("알림 전송에 실패했습니다: " + e.getMessage());
        }
    }
}