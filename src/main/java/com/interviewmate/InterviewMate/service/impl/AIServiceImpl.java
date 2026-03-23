package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class AIServiceImpl implements AIService {

    @Override
    public List<String> generateQuestions(String perfilProfesional, String tipoEntrevista, String nivelDificultad) {
        log.info("Generando preguntas para perfil: {}, tipo: {}, dificultad: {}",
                perfilProfesional.substring(0, Math.min(50, perfilProfesional.length())),
                tipoEntrevista, nivelDificultad);

        // TODO: Integrar con API de IA (OpenAI, Gemini, etc.)
        // Por ahora retornamos preguntas de ejemplo basadas en el tipo y dificultad

        String prompt = String.format(
            "Genera 5 preguntas técnicas para un rol de %s nivel %s basándote en este CV: %s",
            tipoEntrevista, nivelDificultad, perfilProfesional
        );

        log.info("Prompt para IA: {}", prompt);

        // Preguntas de ejemplo - reemplazar con llamada real a IA
        return getSampleQuestions(tipoEntrevista, nivelDificultad);
    }

    private List<String> getSampleQuestions(String tipoEntrevista, String nivelDificultad) {
        if ("desarrollador backend".equalsIgnoreCase(tipoEntrevista)) {
            if ("junior".equalsIgnoreCase(nivelDificultad)) {
                return Arrays.asList(
                    "¿Qué es una API REST y cuáles son sus principios principales?",
                    "Explica la diferencia entre HTTP GET y POST.",
                    "¿Qué es una base de datos relacional y cuáles son sus ventajas?",
                    "Describe el patrón MVC en el desarrollo web.",
                    "¿Qué es el control de versiones y por qué es importante?"
                );
            } else if ("senior".equalsIgnoreCase(nivelDificultad)) {
                return Arrays.asList(
                    "¿Cómo diseñarías una arquitectura de microservicios para una aplicación de alta escalabilidad?",
                    "Explica el patrón CQRS y en qué escenarios lo implementarías.",
                    "¿Cómo manejarías la consistencia eventual en un sistema distribuido?",
                    "Describe estrategias para optimizar consultas de base de datos en aplicaciones de alto tráfico.",
                    "¿Cómo implementarías un sistema de cache distribuido con Redis?"
                );
            }
        }

        // Preguntas genéricas por defecto
        return Arrays.asList(
            "¿Cuáles son tus fortalezas y debilidades como desarrollador?",
            "Describe un proyecto desafiante en el que hayas trabajado.",
            "¿Cómo te mantienes actualizado con las nuevas tecnologías?",
            "¿Cuál es tu experiencia con metodologías ágiles?",
            "¿Cómo manejas situaciones de presión o deadlines ajustados?"
        );
    }
}
