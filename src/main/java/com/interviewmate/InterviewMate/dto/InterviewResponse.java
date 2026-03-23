package com.interviewmate.InterviewMate.dto;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewResponse {

    private Long id;
    private String title;
    private String description;
    private String status;
    private List<QuestionResponse> questions;
    private Instant createdAt;
    private Instant updatedAt;
}
