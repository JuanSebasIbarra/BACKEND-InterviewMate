package com.interviewmate.InterviewMate.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AiServiceConfigValidator {

    private static final Logger log = LoggerFactory.getLogger(AiServiceConfigValidator.class);

    private final AiServiceProperties aiServiceProperties;

    public AiServiceConfigValidator(AiServiceProperties aiServiceProperties) {
        this.aiServiceProperties = aiServiceProperties;
    }

    @PostConstruct
    public void validate() {
        boolean interviewEnabledFlag = aiServiceProperties.getInterview().isEnabled();
        boolean studyEnabledFlag = aiServiceProperties.getStudy().isEnabled();
        boolean interviewHasKey = !isBlank(aiServiceProperties.getInterview().getApiKey());
        boolean studyHasKey = !isBlank(aiServiceProperties.getStudy().getApiKey());

        if (interviewEnabledFlag && interviewHasKey) {
            log.info("AI ENABLED -> Interview service active (provider={}, model={})",
                    aiServiceProperties.getInterview().getProvider(),
                    aiServiceProperties.getInterview().getChatModel());
        } else {
            log.warn("AI DISABLED -> Interview service in fallback mode (enabledFlag={}, apiKeyPresent={})",
                    interviewEnabledFlag,
                    interviewHasKey);
        }

        if (studyEnabledFlag && studyHasKey) {
            log.info("AI ENABLED -> Study service active (provider={}, model={})",
                    aiServiceProperties.getStudy().getProvider(),
                    aiServiceProperties.getStudy().getTextModel());
        } else {
            log.warn("AI DISABLED -> Study service in fallback mode (enabledFlag={}, apiKeyPresent={})",
                    studyEnabledFlag,
                    studyHasKey);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

