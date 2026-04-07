package com.interviewmate.InterviewMate.controller;

import com.interviewmate.InterviewMate.dto.ApiResponse;
import com.interviewmate.InterviewMate.dto.GenerateStudyQuestionsRequest;
import com.interviewmate.InterviewMate.dto.StartStudyRequest;
import com.interviewmate.InterviewMate.dto.StudySessionResponse;
import com.interviewmate.InterviewMate.dto.StudySessionSummaryResponse;
import com.interviewmate.InterviewMate.service.StudyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/study")
@PreAuthorize("isAuthenticated()")
public class StudyController {

    private final StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
    }

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<StudySessionResponse>> startStudy(@RequestBody StartStudyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(studyService.start(request)));
    }

    @PostMapping("/generate-questions")
    public ResponseEntity<ApiResponse<StudySessionResponse>> generateQuestions(
            @Valid @RequestBody GenerateStudyQuestionsRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.generateQuestions(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudySessionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.getById(id)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<StudySessionSummaryResponse>>> getMyStudySessions() {
        return ResponseEntity.ok(ApiResponse.ok(studyService.getByAuthenticatedUser()));
    }

    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> transcribeAudio(
            @RequestParam("audio") MultipartFile audioFile) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.transcribeAudio(audioFile)));
    }
}
