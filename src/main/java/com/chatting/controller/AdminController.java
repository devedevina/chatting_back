package com.chatting.controller;

import com.chatting.domain.Report;
import com.chatting.domain.Warning;
import com.chatting.dto.ChatRoomResponseDto;
import com.chatting.service.ChatRoomService;
import com.chatting.service.ReportService;
import com.chatting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "관리자 전용 API")
public class AdminController {
    private final ChatRoomService chatRoomService;
    private final ReportService reportService;
    private final UserService userService;

    @GetMapping("/chat-rooms")
    @Operation(summary = "모든 채팅방 조회", description = "관리자가 모든 채팅방(삭제 포함)을 조회합니다")
    public ResponseEntity<List<ChatRoomResponseDto>> getAllChatRooms() {
        List<ChatRoomResponseDto> rooms = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok(rooms);
    }

    @DeleteMapping("/chat-rooms/{roomId}")
    @Operation(summary = "채팅방 강제 삭제", description = "관리자가 채팅방을 강제로 삭제합니다")
    public ResponseEntity<Map<String, String>> deleteChatRoom(@PathVariable Long roomId) {
        chatRoomService.deleteChatRoom(roomId, "ADMIN");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Chat room deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports")
    @Operation(summary = "대기 중인 신고 조회", description = "처리되지 않은 신고 목록을 조회합니다")
    public ResponseEntity<List<Report>> getPendingReports() {
        List<Report> reports = reportService.getPendingReports();
        return ResponseEntity.ok(reports);
    }

    @PostMapping("/reports/{reportId}/approve")
    @Operation(summary = "신고 승인", description = "신고를 승인하고 해당 채팅방을 삭제합니다")
    public ResponseEntity<Map<String, String>> approveReport(@PathVariable Long reportId) {
        reportService.approveReport(reportId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Report approved");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reports/{reportId}/reject")
    @Operation(summary = "신고 거절", description = "신고를 거절합니다")
    public ResponseEntity<Map<String, String>> rejectReport(@PathVariable Long reportId) {
        reportService.rejectReport(reportId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Report rejected");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/warn")
    @Operation(summary = "사용자 경고", description = "사용자에게 경고를 부여합니다. 경고 3회 이상 시 자동 정지됩니다")
    public ResponseEntity<Map<String, String>> warnUser(
            @PathVariable Long userId,
            @RequestParam String reason,
            @RequestParam(required = false) String description) {
        userService.giveWarning(userId, reason, description);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Warning given successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/suspend")
    @Operation(summary = "사용자 일시 정지", description = "사용자를 일정 기간 동안 정지합니다 (기본값: 7일)")
    public ResponseEntity<Map<String, String>> suspendUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "7") Integer days) {
        userService.suspendUser(userId, days);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User suspended for " + days + " days");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/ban")
    @Operation(summary = "사용자 영구 정지", description = "사용자를 영구적으로 정지합니다")
    public ResponseEntity<Map<String, String>> banUser(@PathVariable Long userId) {
        userService.banUser(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User banned permanently");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/unsuspend")
    @Operation(summary = "사용자 정지 해제", description = "사용자의 정지를 해제합니다")
    public ResponseEntity<Map<String, String>> unsuspendUser(@PathVariable Long userId) {
        userService.unsuspendUser(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User unsuspended");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/warnings")
    @Operation(summary = "사용자 경고 이력 조회", description = "사용자의 경고 이력을 조회합니다")
    public ResponseEntity<List<Warning>> getUserWarnings(@PathVariable Long userId) {
        List<Warning> warnings = userService.getUserWarnings(userId);
        return ResponseEntity.ok(warnings);
    }
}
