package com.ibarra.castro.maya.interviewmate.controller;

import com.ibarra.castro.maya.interviewmate.dto.LoginResponse;
import com.ibarra.castro.maya.interviewmate.dto.UserRequest;
import com.ibarra.castro.maya.interviewmate.dto.UserResponse;
import com.ibarra.castro.maya.interviewmate.entity.User;
import com.ibarra.castro.maya.interviewmate.repository.UserRepository;
import com.ibarra.castro.maya.interviewmate.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("User or email already exists");
        }
        User u = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of("ROLE_USER"))
                .createdAt(Instant.now())
                .build();
        userRepository.save(u);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String token = jwtProvider.generateToken(auth.getName());
        Instant expiresAt = Instant.now().plusMillis(jwtProvider.getValidityInMillis());
        LoginResponse resp = LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresAt(expiresAt)
                .username(auth.getName())
                .build();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserResponse res = toResponse(user);
        return ResponseEntity.ok(res);
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .roles(u.getRoles())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
