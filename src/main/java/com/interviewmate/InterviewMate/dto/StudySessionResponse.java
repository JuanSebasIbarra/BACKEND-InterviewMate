package com.interviewmate.InterviewMate.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySessionResponse {

    private UUID id;
    private String topic;
    private LocalDateTime createdAt;
    private List<StudyQuestionResponse> questions;
}

