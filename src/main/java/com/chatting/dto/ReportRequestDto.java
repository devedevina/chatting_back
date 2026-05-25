package com.chatting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequestDto {
    @NotNull(message = "Chat room ID is required")
    private Long chatRoomId;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String description;
}
