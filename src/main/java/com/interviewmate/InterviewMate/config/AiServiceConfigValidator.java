package com.interviewmate.InterviewMate.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AiServiceConfigValidator {

    private final AiServiceProperties aiServiceProperties;

    public AiServiceConfigValidator(AiServiceProperties aiServiceProperties) {
        this.aiServiceProperties = aiServiceProperties;
    }

    @PostConstruct
    public void validate() {
        // Validación deshabilitada: permite API vacía y desactiva servicios AI automáticamente
        // Para usar AI, establece AI_INTERVIEW_API_KEY y/o AI_STUDY_API_KEY
        logAiStatus();
    }

    private void logAiStatus() {
        boolean interviewEnabled = aiServiceProperties.getInterview().isEnabled() && !isBlank(aiServiceProperties.getInterview().getApiKey());
        boolean studyEnabled = aiServiceProperties.getStudy().isEnabled() && !isBlank(aiServiceProperties.getStudy().getApiKey());

        if (!interviewEnabled) {
            System.out.println("⚠️  AI Interview Service disabled (no API key configured)");
        }
        if (!studyEnabled) {
            System.out.println("⚠️  AI Study Service disabled (no API key configured)");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

