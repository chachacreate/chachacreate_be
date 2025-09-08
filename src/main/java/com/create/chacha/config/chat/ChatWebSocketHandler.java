package com.create.chacha.config.chat;

import com.create.chacha.common.util.Base64Util;
import com.create.chacha.domains.shared.chat.service.ChatService;
import com.create.chacha.domains.shared.entity.chat.ChattingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;
import java.util.Map;
import java.util.List;

@Slf4j
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    // 채팅방별 세션 관리
    private final Map<String, Set<WebSocketSession>> chatRoomSessions = new ConcurrentHashMap<>();
    // 사용자별 세션 관리 (사용자 ID -> 세션)
    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String chatroomId = getChatroomIdFromSession(session);
        log.debug("URI: " + session.getUri());
        log.debug("채팅방 ID: " + chatroomId);
        chatRoomSessions.computeIfAbsent(chatroomId, k -> new CopyOnWriteArraySet<>())
                .add(session);

        System.out.println("채팅방 " + chatroomId + "에 연결됨: " + session.getId());

        // 채팅 히스토리 전송
        List<ChattingEntity> history = chatService.getChatHistory(chatroomId);
        for (ChattingEntity chatting : history) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatting)));
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = (String) message.getPayload();
        String chatroomId = getChatroomIdFromSession(session);

        try {
            ChattingEntity chatting = objectMapper.readValue(payload, ChattingEntity.class);
            chatting.setChatroomId(chatroomId);

            // 메시지 저장
            ChattingEntity savedMessage = chatService.saveMessage(chatting);

            // 채팅방 내 모든 세션에 메시지 브로드캐스트
            String broadcastMessage = objectMapper.writeValueAsString(savedMessage);
            Set<WebSocketSession> sessions = chatRoomSessions.get(chatroomId);

            if (sessions != null) {
                for (WebSocketSession webSocketSession : sessions) {
                    if (webSocketSession.isOpen()) {
                        webSocketSession.sendMessage(new TextMessage(broadcastMessage));
                    }
                }
            }

            // 사용자 세션 등록 (알림 등을 위해)
            if (savedMessage.getSenderId() != null) {
                userSessions.put(savedMessage.getSenderId(), session);
            }

        } catch (Exception e) {
            System.err.println("메시지 처리 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String chatroomId = getChatroomIdFromSession(session);
        Set<WebSocketSession> sessions = chatRoomSessions.get(chatroomId);

        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                chatRoomSessions.remove(chatroomId);
            }
        }

        // 사용자 세션에서도 제거
        userSessions.entrySet().removeIf(entry -> entry.getValue().equals(session));

        System.out.println("채팅방 " + chatroomId + "에서 연결 해제됨: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("전송 오류: " + exception.getMessage());
        exception.printStackTrace();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    // 관리자 메시지 전송 (외부에서 호출 가능)
    public void sendAdminMessage(String chatroomId, Long receiverId, String message) {
        try {
            ChattingEntity adminMessage = chatService.saveAdminMessage(chatroomId, receiverId, message);
            String broadcastMessage = objectMapper.writeValueAsString(adminMessage);

            Set<WebSocketSession> sessions = chatRoomSessions.get(chatroomId);
            if (sessions != null) {
                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(broadcastMessage));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("관리자 메시지 전송 오류: " + e.getMessage());
        }
    }

    // 특정 사용자에게 알림 메시지 전송
    public void sendNotificationToUser(Long userId, String notification) {
        try {
            WebSocketSession userSession = userSessions.get(userId);
            if (userSession != null && userSession.isOpen()) {
                userSession.sendMessage(new TextMessage(notification));
            }
        } catch (Exception e) {
            System.err.println("사용자 알림 전송 오류: " + e.getMessage());
        }
    }

    /**
     * WebSocket 세션에서 채팅방 ID 추출 및 Base64 디코딩
     */
    private String getChatroomIdFromSession(WebSocketSession session) {
        URI uri = session.getUri();
        String path = uri.getPath();
        String encodedChatroomId = path.substring(path.lastIndexOf('/') + 1);

        // Base64Util을 사용하여 디코딩
        return Base64Util.decodeChatroomId(encodedChatroomId);
    }
}
