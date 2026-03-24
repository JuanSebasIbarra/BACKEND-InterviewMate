package com.interviewmate.InterviewMate.dto;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private String text;
    private String type;
    private Integer questionOrder;
    private Instant createdAt;
}
