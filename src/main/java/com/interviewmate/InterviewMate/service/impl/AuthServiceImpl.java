package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.dto.AuthResponse;
import com.interviewmate.InterviewMate.dto.LoginRequest;
import com.interviewmate.InterviewMate.dto.RegisterRequest;
import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.exception.BadRequestException;
import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.security.JwtProvider;
import com.interviewmate.InterviewMate.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // ── Register ──────────────────────────────────────────────────────────────

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // 1. Guard: unique username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' is already taken");
        }

        // 2. Guard: unique email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' is already registered");
        }

        // 3. Build user — password is hashed here, never stored in plain text
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of("ROLE_USER"))
                .build();

        User saved = userRepository.save(user);
        log.info("User '{}' registered successfully (id={})", saved.getUsername(), saved.getId());

        // 4. Issue token so the user is immediately authenticated
        return buildAuthResponse(saved);
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        // 1. Find user — return a generic error to avoid username enumeration
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // 2. Verify password against the BCrypt hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Failed login attempt for user '{}'", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        log.info("User '{}' logged in successfully", user.getUsername());
        return buildAuthResponse(user);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtProvider.generateToken(user.getUsername());
        Instant expiresAt = jwtProvider.getExpirationFromToken(token);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .expiresAt(expiresAt)
                .build();
    }
}
