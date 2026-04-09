package com.interviewmate.InterviewMate.dto;

import jakarta.validation.constraints.NotBlank;

public record EvaluationRequest(
        @NotBlank(message = "question is required") String question,
        @NotBlank(message = "userResponse is required") String userResponse
) {
}

