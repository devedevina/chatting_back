package com.chatting.repository;

import com.chatting.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = ?1 ORDER BY m.createdAt DESC LIMIT ?2")
    List<ChatMessage> findLatestMessages(Long chatRoomId, Integer limit);

    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = ?1 ORDER BY m.createdAt ASC")
    List<ChatMessage> findAllByRoomId(Long chatRoomId);
}
