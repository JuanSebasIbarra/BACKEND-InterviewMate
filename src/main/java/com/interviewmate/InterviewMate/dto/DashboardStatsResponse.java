package com.interviewmate.InterviewMate.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {

    private long totalInterviewSessionsCompleted;
    private double avgInterviewScore;
    private long totalStudySessions;
    private long totalInterviewTemplates;
    private LocalDateTime lastInterviewSessionDate;
    private LocalDateTime lastStudySessionDate;
}

