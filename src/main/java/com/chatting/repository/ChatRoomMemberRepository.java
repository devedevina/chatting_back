package com.chatting.repository;

import com.chatting.domain.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    @Query(value = "SELECT m FROM ChatRoomMember m WHERE m.chatRoom.id = ?1 AND m.user.id = ?2")
    Optional<ChatRoomMember> findByChatRoomIdAndUserId(Long roomId, Long userId);

    @Query(value = "SELECT m FROM ChatRoomMember m WHERE m.chatRoom.id = ?1 AND m.isActive = true")
    List<ChatRoomMember> findActiveMembersByRoomId(Long chatRoomId);

    @Query(value = "SELECT COUNT(m) FROM ChatRoomMember m WHERE m.chatRoom.id = ?1 AND m.isActive = true")
    Integer countActiveMembersByRoomId(Long chatRoomId);

    @Query(value = "SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM ChatRoomMember m WHERE m.chatRoom.id = ?1 AND m.user.id = ?2")
    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);
}
