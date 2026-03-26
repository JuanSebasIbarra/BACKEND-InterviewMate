package com.interviewmate.InterviewMate.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private UUID id;
    private UUID sessionId;
    private int orderIndex;
    private String question;
    private String answer;
    private String aiFeedback;
    private Double score;
    private String aiModel;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
}
