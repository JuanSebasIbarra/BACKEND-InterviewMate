package com.interviewmate.InterviewMate.dto;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponse {

    private Long id;
    private Long questionId;
    private String text;
    private Integer score;
    private Instant createdAt;
}
