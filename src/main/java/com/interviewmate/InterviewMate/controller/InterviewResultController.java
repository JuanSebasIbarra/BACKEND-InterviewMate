package com.interviewmate.InterviewMate.controller;

import com.interviewmate.InterviewMate.dto.ApiResponse;
import com.interviewmate.InterviewMate.dto.InterviewResultResponse;
import com.interviewmate.InterviewMate.service.InterviewResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/results")
@PreAuthorize("isAuthenticated()")
public class InterviewResultController {

    private final InterviewResultService resultService;

    public InterviewResultController(InterviewResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<InterviewResultResponse>> getBySession(
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(resultService.getBySession(sessionId)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<InterviewResultResponse>>> getByAuthenticatedUser() {
        return ResponseEntity.ok(ApiResponse.ok(resultService.getByAuthenticatedUser()));
    }
}
