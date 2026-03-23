package com.interviewmate.InterviewMate.controller;

import com.interviewmate.InterviewMate.dto.ProfileRequest;
import com.interviewmate.InterviewMate.dto.ProfileResponse;
import com.interviewmate.InterviewMate.dto.UserRequest;
import com.interviewmate.InterviewMate.dto.UserResponse;
import com.interviewmate.InterviewMate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Page<UserResponse> listUsers(Pageable pageable) {
        return userService.listUsers(pageable);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/perfil")
    public ProfileResponse getProfile() {
        String username = getCurrentUsername();
        return userService.getProfile(username);
    }

    @PutMapping("/perfil")
    public ProfileResponse updateProfile(@Valid @RequestBody ProfileRequest request) {
        String username = getCurrentUsername();
        return userService.updateProfile(username, request);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
