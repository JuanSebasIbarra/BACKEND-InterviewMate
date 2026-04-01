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
        validateInterviewMode();
        validateStudyMode();
    }

    private void validateInterviewMode() {
        if (aiServiceProperties.getInterview().isEnabled() && isBlank(aiServiceProperties.getInterview().getApiKey())) {
            throw new IllegalStateException("Missing AI key: define AI_INTERVIEW_API_KEY or app.ai.interview.api-key");
        }
    }

    private void validateStudyMode() {
        if (aiServiceProperties.getStudy().isEnabled() && isBlank(aiServiceProperties.getStudy().getApiKey())) {
            throw new IllegalStateException("Missing AI key: define AI_STUDY_API_KEY or app.ai.study.api-key");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

