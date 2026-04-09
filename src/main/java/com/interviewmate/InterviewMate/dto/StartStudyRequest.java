package com.interviewmate.InterviewMate.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartStudyRequest {

    @NotNull(message = "Template ID is required")
    private UUID templateId;
    private String topic;
}

