package com.interviewmate.InterviewMate.service;

import java.util.List;

public interface AIService {

    /**
     * Genera preguntas técnicas basadas en el perfil del usuario
     * @param perfilProfesional Texto del perfil/CV del usuario
     * @param tipoEntrevista Tipo de entrevista (ej: "desarrollador backend")
     * @param nivelDificultad Nivel de dificultad (ej: "junior", "senior")
     * @return Lista de preguntas generadas
     */
    List<String> generateQuestions(String perfilProfesional, String tipoEntrevista, String nivelDificultad);
}
