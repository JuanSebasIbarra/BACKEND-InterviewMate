package com.interviewmate.InterviewMate.controller;

import com.interviewmate.InterviewMate.dto.ApiResponse;
import com.interviewmate.InterviewMate.dto.CreateSessionRequest;
import com.interviewmate.InterviewMate.dto.SessionResponse;
import com.interviewmate.InterviewMate.service.InterviewSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessions")
@PreAuthorize("isAuthenticated()")
public class InterviewSessionController {

    private final InterviewSessionService sessionService;

    public InterviewSessionController(InterviewSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SessionResponse>> create(
            @Valid @RequestBody CreateSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(sessionService.create(request)));
    }

    @PatchMapping("/{sessionId}/begin")
    public ResponseEntity<ApiResponse<SessionResponse>> begin(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.begin(sessionId)));
    }

    @PatchMapping("/{sessionId}/complete")
    public ResponseEntity<ApiResponse<SessionResponse>> complete(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.complete(sessionId)));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<SessionResponse>> getById(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getById(sessionId)));
    }

    @GetMapping("/template/{templateId}")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getAllByTemplate(
            @PathVariable UUID templateId) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getAllByTemplate(templateId)));
    }
}
