package com.create.chacha.domains.shared.chat.service;

import com.create.chacha.domains.shared.chat.dto.response.ChatRoomResponseDTO;
import com.create.chacha.domains.shared.entity.chat.ChatRoomEntity;
import com.create.chacha.domains.shared.entity.chat.ChattingEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;

import java.util.List;

public interface ChatService {
    // 1대1 채팅방 생성 또는 찾기
    public ChatRoomEntity createOrFindPersonalChatRoom(Long buyerId, String storeUrl);

    // 단체 채팅방 생성
    public ChatRoomEntity createGroupChatRoom(List<Long> memberIds);

    // 관리자 채팅방 생성 또는 찾기
    public ChatRoomEntity createOrFindAdminChatRoom(Long memberId);

    // 메시지 저장
    public ChattingEntity saveMessage(ChattingEntity chatting);

    // 관리자 메시지 저장
    public ChattingEntity saveAdminMessage(String chatroomId, Long receiverId, String message);

    // 채팅방 메시지 조회
    public List<ChattingEntity> getChatHistory(String chatroomId);

    // 사용자 채팅방 목록 조회
    List<ChatRoomResponseDTO> getUserChatRooms(Long memberId);

    // 사용자 채팅방 목록 조회 Entity 반환 메서드
    List<ChatRoomEntity> getUserChatRoomEntities(Long memberId);

    // 읽음 처리
    public void updateLastReadAt(String chatroomId, Long memberId);

    // 회원 정보 조회
    public MemberEntity getMemberById(Long memberId);

    // 이메일로 회원 조회 (로그인 등에서 사용)
    public MemberEntity getMemberByEmail(String email);

    // 회원 이름으로 채팅방 검색 (AES 복호화 고려)
    public List<ChatRoomEntity> searchChatRoomsByMemberName(String memberName);

    // 회원 상세 정보 조회 (개인정보 마스킹 처리)
    public MemberEntity getMemberInfoForChat(Long memberId);

}
