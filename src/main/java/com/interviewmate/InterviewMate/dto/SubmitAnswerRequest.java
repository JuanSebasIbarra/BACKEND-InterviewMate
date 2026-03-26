package com.interviewmate.InterviewMate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitAnswerRequest {

    @NotBlank(message = "Answer is required")
    private String answer;
}
