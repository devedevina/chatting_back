package com.chatting.service;

import com.chatting.domain.User;
import com.chatting.dto.AuthRequestDto;
import com.chatting.dto.AuthResponseDto;
import com.chatting.dto.RegisterRequestDto;
import com.chatting.repository.UserRepository;
import com.chatting.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("Nickname already exists");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .email(request.getEmail())
                .role(User.UserRole.USER)
                .status(User.UserStatus.ACTIVE)
                .warningCount(0)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(request.getUsername());

        return AuthResponseDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .nickname(savedUser.getNickname())
                .token(token)
                .role(savedUser.getRole().toString())
                .build();
    }

    public AuthResponseDto login(AuthRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtTokenProvider.generateToken(authentication);

        return AuthResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .token(token)
                .role(user.getRole().toString())
                .build();
    }
}
