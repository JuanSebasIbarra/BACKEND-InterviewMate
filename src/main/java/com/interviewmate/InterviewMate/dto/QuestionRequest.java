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
public class QuestionRequest {

    @NotBlank(message = "El texto de la pregunta es obligatorio")
    @Size(min = 1, max = 1000, message = "La pregunta debe tener entre 1 y 1000 caracteres")
    private String text;

    @NotBlank(message = "El tipo de pregunta es obligatorio")
    @Size(max = 100, message = "El tipo no puede exceder 100 caracteres")
    private String type;

    private Integer questionOrder; // Orden de la pregunta

    // Opcional: opciones para preguntas de opción múltiple (puede quedar null si no aplica)
    private List<String> options;
}
