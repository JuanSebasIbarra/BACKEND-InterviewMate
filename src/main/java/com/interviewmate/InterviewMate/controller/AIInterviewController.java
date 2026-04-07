package com.interviewmate.InterviewMate.controller;

import com.interviewmate.InterviewMate.dto.EvaluationRequest;
import com.interviewmate.InterviewMate.dto.EvaluationResult;
import com.interviewmate.InterviewMate.service.AiInterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/interview")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class AIInterviewController {

	private final AiInterviewService aiInterviewService;

	@PostMapping("/evaluate")
	public ResponseEntity<EvaluationResult> evaluate(@Valid @RequestBody EvaluationRequest request) {
		EvaluationResult result = aiInterviewService.evaluateResponse(request.question(), request.userResponse());
		return ResponseEntity.ok(result);
	}
}

