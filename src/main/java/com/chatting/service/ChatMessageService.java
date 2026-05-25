package com.chatting.service;

import com.chatting.domain.ChatMessage;
import com.chatting.domain.ChatRoom;
import com.chatting.domain.User;
import com.chatting.dto.ChatMessageDto;
import com.chatting.repository.ChatMessageRepository;
import com.chatting.repository.ChatRoomRepository;
import com.chatting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageDto sendMessage(Long roomId, String content, String username) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .messageType(ChatMessage.MessageType.USER)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        return ChatMessageDto.fromEntity(savedMessage);
    }

    @Transactional
    public ChatMessageDto createSystemMessage(Long roomId, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(null)
                .content(content)
                .messageType(ChatMessage.MessageType.SYSTEM)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        return ChatMessageDto.fromEntity(savedMessage);
    }

    public List<ChatMessageDto> getChatHistory(Long roomId) {
        return chatMessageRepository.findAllByRoomId(roomId).stream()
                .map(ChatMessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ChatMessageDto> getLatestMessages(Long roomId, Integer limit) {
        return chatMessageRepository.findLatestMessages(roomId, limit).stream()
                .map(ChatMessageDto::fromEntity)
                .collect(Collectors.toList());
    }
}
