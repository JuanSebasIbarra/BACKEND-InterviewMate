package com.interviewmate.InterviewMate.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartInterviewRequest {

    @NotBlank(message = "Tipo de entrevista es obligatorio")
    private String tipoEntrevista;

    @NotBlank(message = "Nivel de dificultad es obligatorio")
    private String nivelDificultad;
}
