package com.chatting.controller;

import com.chatting.dto.ChatMessageDto;
import com.chatting.dto.WebSocketMessageDto;
import com.chatting.service.ChatMessageService;
import com.chatting.service.ChatRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Tag(name = "WebSocket Chat", description = "실시간 채팅 WebSocket API")
public class ChatController {
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @MessageMapping("/chat/{roomId}/send")
    @SendTo("/topic/chat/{roomId}")
    public WebSocketMessageDto sendMessage(
            @DestinationVariable Long roomId,
            @Payload WebSocketMessageDto message,
            SimpMessageHeaderAccessor headerAccessor) {

        String username = null;
        if (headerAccessor.getSessionAttributes() != null) {
            username = (String) headerAccessor.getSessionAttributes().get("username");
        }

        Principal principal = headerAccessor.getUser();

        if (username == null && principal != null) {
            username = principal.getName();
        }

        ChatMessageDto savedMessage = chatMessageService.sendMessage(roomId, message.getContent(), username);

        return WebSocketMessageDto.builder()
                .roomId(roomId)
                .senderNickname(savedMessage.getSenderNickname())
                .content(savedMessage.getContent())
                .messageType(savedMessage.getMessageType())
                .timestamp(savedMessage.getCreatedAt().format(formatter))
                .build();
    }

    @MessageMapping("/chat/{roomId}/connect")
    public void handleUserConnect(
            @DestinationVariable Long roomId,
            SimpMessageHeaderAccessor headerAccessor) {
        String username = null;
        if (headerAccessor.getSessionAttributes() != null) {
            username = (String) headerAccessor.getSessionAttributes().get("username");
        }
        if (username == null) {
            throw new IllegalArgumentException("Username not found in session");
        }
        chatRoomService.handleUserConnected(roomId, username);
    }

    @MessageMapping("/chat/{roomId}/disconnect")
    public void handleUserDisconnect(
            @DestinationVariable Long roomId,
            SimpMessageHeaderAccessor headerAccessor) {
        String username = null;
        if (headerAccessor.getSessionAttributes() != null) {
            username = (String) headerAccessor.getSessionAttributes().get("username");
        }
        if (username == null) {
            throw new IllegalArgumentException("Username not found in session");
        }
        chatRoomService.handleUserDisconnected(roomId, username);
    }
}
