package com.interviewmate.InterviewMate.controller;

import com.interviewmate.InterviewMate.dto.ApiResponse;
import com.interviewmate.InterviewMate.dto.QuestionResponse;
import com.interviewmate.InterviewMate.dto.SubmitAnswerRequest;
import com.interviewmate.InterviewMate.service.InterviewQuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/questions")
@PreAuthorize("isAuthenticated()")
public class InterviewQuestionController {

    private final InterviewQuestionService questionService;

    public InterviewQuestionController(InterviewQuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getBySession(
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(questionService.getBySession(sessionId)));
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<ApiResponse<QuestionResponse>> getById(@PathVariable UUID questionId) {
        return ResponseEntity.ok(ApiResponse.ok(questionService.getById(questionId)));
    }

    @PatchMapping("/{questionId}/answer")
    public ResponseEntity<ApiResponse<QuestionResponse>> submitAnswer(
            @PathVariable UUID questionId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(questionService.submitAnswer(questionId, request)));
    }
}
