package com.interviewmate.InterviewMate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Resultado estructurado de la evaluación de respuesta de entrevista.
 * Utilizado por Google Gemini para devolver evaluaciones técnicas consistentes.
 */
public record EvaluationResult(
    int score,
    List<String> strengths,
    List<String> codeSmells,
    String technicalFeedback,
    String suggestedImprovement
) {
    public EvaluationResult {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
    }

    @JsonProperty("feedback")
    public String feedback() {
        return technicalFeedback;
    }
}

