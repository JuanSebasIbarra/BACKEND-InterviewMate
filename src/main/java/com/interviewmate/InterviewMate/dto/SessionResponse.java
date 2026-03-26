package com.interviewmate.InterviewMate.dto;

import com.interviewmate.InterviewMate.enums.SessionStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionResponse {

    private UUID id;
    private int attemptNumber;
    private SessionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private UUID templateId;
    private String templatePosition;
    private String templateEnterprise;
}
