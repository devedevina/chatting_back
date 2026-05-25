package com.chatting.service;

import com.chatting.domain.ChatRoom;
import com.chatting.domain.ChatRoomMember;
import com.chatting.domain.User;
import com.chatting.dto.ChatRoomRequestDto;
import com.chatting.dto.ChatRoomResponseDto;
import com.chatting.repository.ChatRoomMemberRepository;
import com.chatting.repository.ChatRoomRepository;
import com.chatting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<ChatRoomResponseDto> getPublicChatRooms() {
        return chatRoomRepository.findAllPublicRooms().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<ChatRoomResponseDto> getAllChatRooms() {
        return chatRoomRepository.findAllNotDeleted().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatRoomResponseDto createChatRoom(ChatRoomRequestDto request, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String encodedPassword = null;
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            encodedPassword = passwordEncoder.encode(request.getPassword());
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .creator(creator)
                .isPublic(request.getIsPublic())
                .password(encodedPassword)
                .maxMembers(request.getMaxMembers())
                .currentMembers(1)
                .isDeleted(false)
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        ChatRoomMember member = ChatRoomMember.builder()
                .chatRoom(savedRoom)
                .user(creator)
                .isActive(true)
                .build();
        chatRoomMemberRepository.save(member);

        return toResponseDto(savedRoom);
    }

    @Transactional
    public void joinChatRoom(Long roomId, String password, String username) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        if (chatRoom.getIsDeleted()) {
            throw new IllegalArgumentException("Chat room is deleted");
        }

        if (chatRoom.getCurrentMembers() >= chatRoom.getMaxMembers()) {
            throw new IllegalArgumentException("Chat room is full");
        }

        if (chatRoom.getPassword() != null && !chatRoom.getPassword().isEmpty()) {
            if (password == null || !passwordEncoder.matches(password, chatRoom.getPassword())) {
                throw new IllegalArgumentException("Incorrect password");
            }
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (chatRoomMemberRepository.existsByChatRoomIdAndUserId(roomId, user.getId())) {
            throw new IllegalArgumentException("Already in chat room");
        }

        ChatRoomMember member = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .user(user)
                .isActive(true)
                .build();
        chatRoomMemberRepository.save(member);

        chatRoom.setCurrentMembers(chatRoom.getCurrentMembers() + 1);
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void leaveChatRoom(Long roomId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ChatRoomMember member = chatRoomMemberRepository.findByChatRoomIdAndUserId(roomId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Not in chat room"));

        member.setIsActive(false);
        member.setLeftAt(java.time.LocalDateTime.now());
        chatRoomMemberRepository.save(member);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        chatRoom.setCurrentMembers(Math.max(0, chatRoom.getCurrentMembers() - 1));
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void deleteChatRoom(Long roomId, String username) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        if (!chatRoom.getCreator().getUsername().equals(username)) {
            throw new IllegalArgumentException("Only creator can delete the chat room");
        }

        chatRoom.setIsDeleted(true);
        chatRoom.setDeletedAt(java.time.LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
    }

    public ChatRoomResponseDto getChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        return toResponseDto(chatRoom);
    }

    private ChatRoomResponseDto toResponseDto(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .description(chatRoom.getDescription())
                .creatorNickname(chatRoom.getCreator().getNickname())
                .isPublic(chatRoom.getIsPublic())
                .isPasswordProtected(chatRoom.getPassword() != null && !chatRoom.getPassword().isEmpty())
                .currentMembers(chatRoom.getCurrentMembers())
                .maxMembers(chatRoom.getMaxMembers())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}
