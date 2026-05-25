package com.chatting.controller;

import com.chatting.dto.ReportRequestDto;
import com.chatting.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "신고 관련 API")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "채팅방 신고", description = "부적절한 채팅방을 신고합니다. 인증 필요")
    public ResponseEntity<Map<String, String>> reportChatRoom(
            @Valid @RequestBody ReportRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        reportService.reportChatRoom(request, userDetails.getUsername());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Chat room reported successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
