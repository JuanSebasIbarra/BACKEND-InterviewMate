package com.interviewmate.InterviewMate.controller;

import com.interviewmate.InterviewMate.dto.ApiResponse;
import com.interviewmate.InterviewMate.dto.CreateInterviewTemplateRequest;
import com.interviewmate.InterviewMate.dto.InterviewTemplateResponse;
import com.interviewmate.InterviewMate.dto.UpdateInterviewTemplateRequest;
import com.interviewmate.InterviewMate.enums.InterviewStatus;
import com.interviewmate.InterviewMate.service.InterviewTemplateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/interview-templates")
@PreAuthorize("isAuthenticated()")
public class InterviewTemplateController {

    private final InterviewTemplateService templateService;

    public InterviewTemplateController(InterviewTemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InterviewTemplateResponse>> create(
            @Valid @RequestBody CreateInterviewTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(templateService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InterviewTemplateResponse>>> getAllByAuthenticatedUser() {
        return ResponseEntity.ok(ApiResponse.ok(templateService.getAllByAuthenticatedUser()));
    }

    @GetMapping("/{templateId}")
    public ResponseEntity<ApiResponse<InterviewTemplateResponse>> getById(
            @PathVariable UUID templateId) {
        return ResponseEntity.ok(ApiResponse.ok(templateService.getById(templateId)));
    }

    @PatchMapping("/{templateId}")
    public ResponseEntity<ApiResponse<InterviewTemplateResponse>> update(
            @PathVariable UUID templateId,
            @RequestBody UpdateInterviewTemplateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(templateService.update(templateId, request)));
    }

    @PatchMapping("/{templateId}/status")
    public ResponseEntity<ApiResponse<InterviewTemplateResponse>> changeStatus(
            @PathVariable UUID templateId,
            @RequestParam InterviewStatus newStatus) {
        return ResponseEntity.ok(ApiResponse.ok(templateService.changeStatus(templateId, newStatus)));
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID templateId) {
        templateService.delete(templateId);
        return ResponseEntity.ok(ApiResponse.ok("Template archived successfully", null));
    }
}
