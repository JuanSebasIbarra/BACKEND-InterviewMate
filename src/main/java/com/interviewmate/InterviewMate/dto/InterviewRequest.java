package com.interviewmate.InterviewMate.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRequest {

    @NotBlank(message = "Titulo debe ser obligatorio")
    @Size(min = 3, max = 255)
    private String title;

    @Size(max = 1000)
    private String description;

    private List<QuestionRequest> questions;
}

