package com.chatting.service;

import com.chatting.domain.User;
import com.chatting.domain.Warning;
import com.chatting.repository.UserRepository;
import com.chatting.repository.WarningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WarningRepository warningRepository;

    @Transactional
    public void giveWarning(Long userId, String reason, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Warning warning = Warning.builder()
                .user(user)
                .reason(reason)
                .description(description)
                .build();

        warningRepository.save(warning);

        Integer warningCount = warningRepository.countByUserId(userId);
        user.setWarningCount(warningCount);

        if (warningCount >= 3) {
            user.setStatus(User.UserStatus.SUSPENDED);
            user.setSuspendedUntil(LocalDateTime.now().plusDays(7));
        }

        userRepository.save(user);
    }

    @Transactional
    public void suspendUser(Long userId, Integer days) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setStatus(User.UserStatus.SUSPENDED);
        user.setSuspendedUntil(LocalDateTime.now().plusDays(days));
        userRepository.save(user);
    }

    @Transactional
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setStatus(User.UserStatus.BANNED);
        user.setSuspendedUntil(null);
        userRepository.save(user);
    }

    public List<Warning> getUserWarnings(Long userId) {
        return warningRepository.findByUserId(userId);
    }

    public Integer getUserWarningCount(Long userId) {
        return warningRepository.countByUserId(userId);
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public void unsuspendUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getStatus() == User.UserStatus.SUSPENDED) {
            user.setStatus(User.UserStatus.ACTIVE);
            user.setSuspendedUntil(null);
            userRepository.save(user);
        }
    }
}
