package com.chatting.service;

import com.chatting.domain.ChatRoom;
import com.chatting.domain.Report;
import com.chatting.domain.User;
import com.chatting.dto.ReportRequestDto;
import com.chatting.repository.ChatRoomRepository;
import com.chatting.repository.ReportRepository;
import com.chatting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public void reportChatRoom(ReportRequestDto request, String username) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        User reporter = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Report report = Report.builder()
                .chatRoom(chatRoom)
                .reporter(reporter)
                .reason(Report.ReportReason.valueOf(request.getReason()))
                .description(request.getDescription())
                .status(Report.ReportStatus.PENDING)
                .build();

        reportRepository.save(report);
    }

    public List<Report> getPendingReports() {
        return reportRepository.findPendingReports();
    }

    @Transactional
    public void approveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        report.setStatus(Report.ReportStatus.APPROVED);
        report.setProcessedAt(java.time.LocalDateTime.now());
        reportRepository.save(report);
    }

    @Transactional
    public void rejectReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        report.setStatus(Report.ReportStatus.REJECTED);
        report.setProcessedAt(java.time.LocalDateTime.now());
        reportRepository.save(report);
    }

    public List<Report> getChatRoomReports(Long chatRoomId) {
        return reportRepository.findByChatRoomId(chatRoomId);
    }

    public Integer getApprovedReportCount(Long chatRoomId) {
        return reportRepository.countApprovedReportsByChatRoom(chatRoomId);
    }
}
