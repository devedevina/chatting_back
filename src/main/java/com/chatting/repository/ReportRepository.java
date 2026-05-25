package com.chatting.repository;

import com.chatting.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' ORDER BY r.createdAt ASC")
    List<Report> findPendingReports();

    @Query("SELECT r FROM Report r WHERE r.chatRoom.id = ?1 ORDER BY r.createdAt DESC")
    List<Report> findByChatRoomId(Long chatRoomId);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.chatRoom.id = ?1 AND r.status = 'APPROVED'")
    Integer countApprovedReportsByChatRoom(Long chatRoomId);
}
