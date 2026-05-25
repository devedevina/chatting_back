package com.chatting.dto;

import com.chatting.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    private Long chatRoomId;
    private String senderNickname;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;

    public static ChatMessageDto fromEntity(ChatMessage message) {
        return ChatMessageDto.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .senderNickname(message.getSender() != null ? message.getSender().getNickname() : "SYSTEM")
                .content(message.getContent())
                .messageType(message.getMessageType().toString())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
