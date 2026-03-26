package com.interviewmate.InterviewMate.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateResultRequest {

    private String generalFeedback;
    private String strengths;
    private String weaknesses;
    private Double totalScore;
    private String aiModel;
    private int totalTokensUsed;
}
