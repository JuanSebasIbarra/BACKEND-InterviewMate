package com.interviewmate.InterviewMate.dto;

import java.util.List;

/**
 * Resultado estructurado de la generación de preguntas de estudio.
 * Utilizado por Google Gemini para devolver preguntas consistentes con metadatos.
 */
public record StudyQuestionDto(
    String questionText,
    String difficulty,
    String type,
    String rationale
) {}


