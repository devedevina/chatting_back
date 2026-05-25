package com.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponseDto {
    private Long id;
    private String title;
    private String description;
    private String creatorNickname;
    private Boolean isPublic;
    private Boolean isPasswordProtected;
    private Integer currentMembers;
    private Integer maxMembers;
    private LocalDateTime createdAt;
}
