package com.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessageDto {
    private Long roomId;
    private String senderNickname;
    private String content;
    private String messageType;
    private String timestamp;
}
