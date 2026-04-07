package com.interviewmate.InterviewMate.dto;

import com.interviewmate.InterviewMate.enums.InterviewType;
import com.interviewmate.InterviewMate.enums.ResultStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewResultResponse {

    private UUID id;
    private UUID sessionId;
    private int attemptNumber;
    // Template context
    private UUID templateId;
    private String enterprise;
    private String position;
    private InterviewType interviewType;
    // Result fields
    private String generalFeedback;
    private String strengths;
    private String weaknesses;
    private Double totalScore;
    private ResultStatus status;
    private String aiModel;
    private int totalTokensUsed;
    private LocalDateTime generatedAt;
    private LocalDateTime sessionStartedAt;
}
