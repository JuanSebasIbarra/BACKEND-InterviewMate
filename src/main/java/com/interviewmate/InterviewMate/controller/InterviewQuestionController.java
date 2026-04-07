package com.interviewmate.InterviewMate.controller;

import com.interviewmate.InterviewMate.dto.ApiResponse;
import com.interviewmate.InterviewMate.dto.QuestionResponse;
import com.interviewmate.InterviewMate.dto.SubmitAnswerRequest;
import com.interviewmate.InterviewMate.service.AiInterviewService;
import com.interviewmate.InterviewMate.service.InterviewQuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private final AiInterviewService aiInterviewService;

    public InterviewQuestionController(InterviewQuestionService questionService,
                                       AiInterviewService aiInterviewService) {
        this.questionService = questionService;
        this.aiInterviewService = aiInterviewService;
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

    @GetMapping(value = "/{questionId}/tts", produces = "audio/mpeg")
    public ResponseEntity<byte[]> getQuestionTts(@PathVariable UUID questionId) {
        byte[] audio = aiInterviewService.generateSpeechForQuestion(questionId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=question-" + questionId + ".mp3")
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(audio);
    }
}
