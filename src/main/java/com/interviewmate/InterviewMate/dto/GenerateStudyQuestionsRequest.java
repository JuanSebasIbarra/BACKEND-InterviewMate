package com.interviewmate.InterviewMate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateStudyQuestionsRequest {

    @NotNull(message = "Study session ID is required")
    private UUID studySessionId;
}

