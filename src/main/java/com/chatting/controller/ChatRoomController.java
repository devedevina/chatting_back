package com.chatting.controller;

import com.chatting.dto.ChatRoomRequestDto;
import com.chatting.dto.ChatRoomResponseDto;
import com.chatting.service.ChatRoomService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
@Tag(name = "Chat Rooms", description = "채팅방 관리 API")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/public")
    @Operation(summary = "공개 채팅방 목록 조회", description = "모든 사용자가 조회할 수 있는 공개 채팅방 목록을 반환합니다")
    public ResponseEntity<List<ChatRoomResponseDto>> getPublicChatRooms() {
        List<ChatRoomResponseDto> rooms = chatRoomService.getPublicChatRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping
    @Operation(summary = "모든 채팅방 조회", description = "인증된 사용자가 모든 채팅방을 조회합니다 (공개/비공개 포함)")
    public ResponseEntity<List<ChatRoomResponseDto>> getAllChatRooms() {
        List<ChatRoomResponseDto> rooms = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok(rooms);
    }

    @PostMapping
    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다. 인증 필요")
    public ResponseEntity<ChatRoomResponseDto> createChatRoom(
            @Valid @RequestBody ChatRoomRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ChatRoomResponseDto response = chatRoomService.createChatRoom(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "특정 채팅방 조회", description = "특정 ID의 채팅방 정보를 조회합니다")
    public ResponseEntity<ChatRoomResponseDto> getChatRoom(@PathVariable Long roomId) {
        ChatRoomResponseDto room = chatRoomService.getChatRoom(roomId);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/{roomId}/join")
    @Operation(summary = "채팅방 입장", description = "채팅방에 입장합니다. 비밀번호 채팅방의 경우 password 파라미터 필요")
    public ResponseEntity<Map<String, String>> joinChatRoom(
            @PathVariable Long roomId,
            @RequestParam(required = false) String password,
            @AuthenticationPrincipal UserDetails userDetails) {
        chatRoomService.joinChatRoom(roomId, password, userDetails.getUsername());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully joined chat room");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomId}/leave")
    @Operation(summary = "채팅방 퇴장", description = "채팅방에서 나갑니다")
    public ResponseEntity<Map<String, String>> leaveChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        chatRoomService.leaveChatRoom(roomId, userDetails.getUsername());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully left chat room");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{roomId}")
    @Operation(summary = "채팅방 삭제", description = "채팅방을 삭제합니다. 채팅방 생성자만 가능")
    public ResponseEntity<Map<String, String>> deleteChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        chatRoomService.deleteChatRoom(roomId, userDetails.getUsername());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Chat room deleted successfully");
        return ResponseEntity.ok(response);
    }
}
