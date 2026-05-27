package com.chatting.repository;

import com.chatting.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c WHERE c.isDeleted = false AND c.isActive = true AND c.isPublic = true ORDER BY c.createdAt DESC")
    List<ChatRoom> findAllPublicRooms();

    @Query("SELECT c FROM ChatRoom c WHERE c.isDeleted = false AND c.isActive = true ORDER BY c.createdAt DESC")
    List<ChatRoom> findAllNotDeleted();

    @Query("SELECT c FROM ChatRoom c WHERE c.creator.id = ?1 AND c.isDeleted = false AND c.isActive = true")
    List<ChatRoom> findByCreatorId(Long creatorId);
}
