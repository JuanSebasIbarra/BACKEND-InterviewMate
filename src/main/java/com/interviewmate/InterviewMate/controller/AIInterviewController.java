package com.interviewmate.InterviewMate.controller;

import com.interviewmate.InterviewMate.dto.ApiResponse;
import com.interviewmate.InterviewMate.dto.InterviewResultResponse;
import com.interviewmate.InterviewMate.service.AiInterviewService;
import com.interviewmate.InterviewMate.service.InterviewResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/interview")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class AIInterviewController {

	private final AiInterviewService aiInterviewService;
	private final InterviewResultService interviewResultService;

	@PostMapping("/sessions/{sessionId}/review")
	public ResponseEntity<ApiResponse<InterviewResultResponse>> reviewSession(@PathVariable UUID sessionId) {
		aiInterviewService.generateResult(sessionId);
		return ResponseEntity.ok(ApiResponse.ok(interviewResultService.getBySession(sessionId)));
	}
}

