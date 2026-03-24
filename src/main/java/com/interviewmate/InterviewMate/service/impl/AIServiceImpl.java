package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.service.AIService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AIServiceImpl implements AIService {

    private final OpenAiService openAiService;

    public AIServiceImpl(@Value("${app.openai.api-key:}") String apiKey) {
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
            log.info("OpenAI service initialized");
        } else {
            this.openAiService = null;
            log.warn("OpenAI API key not configured, using sample questions");
        }
    }

    @Override
    public List<String> generateQuestions(String perfilProfesional, String tipoEntrevista, String nivelDificultad) {
        log.info("Generando preguntas para perfil: {}, tipo: {}, dificultad: {}",
                perfilProfesional.substring(0, Math.min(50, perfilProfesional.length())),
                tipoEntrevista, nivelDificultad);

        if (openAiService != null) {
            return generateQuestionsWithAI(perfilProfesional, tipoEntrevista, nivelDificultad);
        } else {
            log.warn("Usando preguntas de ejemplo ya que OpenAI no está configurado");
            return getSampleQuestions(tipoEntrevista, nivelDificultad);
        }
    }

    private List<String> generateQuestionsWithAI(String perfilProfesional, String tipoEntrevista, String nivelDificultad) {
        try {
            String prompt = String.format(
                "Genera exactamente 5 preguntas técnicas para un rol de %s nivel %s. " +
                "Las preguntas deben ser específicas y relevantes para el perfil del candidato. " +
                "Basándote en este CV/perfil profesional: %s\n\n" +
                "Formato de respuesta: Lista numerada con las 5 preguntas, una por línea.",
                tipoEntrevista, nivelDificultad, perfilProfesional
            );

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(Arrays.asList(
                            new ChatMessage(ChatMessageRole.SYSTEM.value(),
                                "Eres un reclutador técnico experto que genera preguntas de entrevista relevantes."),
                            new ChatMessage(ChatMessageRole.USER.value(), prompt)
                    ))
                    .maxTokens(1000)
                    .temperature(0.7)
                    .build();

            ChatCompletionResult response = openAiService.createChatCompletion(request);
            String aiResponse = response.getChoices().get(0).getMessage().getContent();

            log.info("Respuesta de IA: {}", aiResponse);

            // Parsear la respuesta para extraer las preguntas
            return parseQuestionsFromResponse(aiResponse);

        } catch (Exception e) {
            log.error("Error al generar preguntas con IA, usando preguntas de ejemplo", e);
            return getSampleQuestions(tipoEntrevista, nivelDificultad);
        }
    }

    private List<String> parseQuestionsFromResponse(String aiResponse) {
        // Dividir por líneas y filtrar preguntas numeradas
        return Arrays.stream(aiResponse.split("\n"))
                .map(String::trim)
                .filter(line -> line.matches("^\\d+\\..*") || line.matches("^\\d+\\s*-.*"))
                .map(line -> line.replaceFirst("^\\d+\\s*[-.]\\s*", "")) // Remover numeración
                .filter(question -> !question.isEmpty() && question.length() > 10)
                .limit(5)
                .collect(Collectors.toList());
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

