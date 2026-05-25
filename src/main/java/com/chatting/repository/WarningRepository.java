package com.chatting.repository;

import com.chatting.domain.Warning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WarningRepository extends JpaRepository<Warning, Long> {
    @Query("SELECT w FROM Warning w WHERE w.user.id = ?1 ORDER BY w.createdAt DESC")
    List<Warning> findByUserId(Long userId);

    @Query("SELECT COUNT(w) FROM Warning w WHERE w.user.id = ?1")
    Integer countByUserId(Long userId);
}
