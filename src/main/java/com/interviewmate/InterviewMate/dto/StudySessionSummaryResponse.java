package com.interviewmate.InterviewMate.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySessionSummaryResponse {

    private UUID id;
    private String topic;
    private int questionCount;
    private LocalDateTime createdAt;
}

