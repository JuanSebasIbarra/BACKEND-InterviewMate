package com.interviewmate.InterviewMate.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitAnswerRequest {

    @NotNull(message = "ID de pregunta es obligatorio")
    private Long questionId;

    @NotBlank(message = "Texto de respuesta es obligatorio")
    private String text;
}
