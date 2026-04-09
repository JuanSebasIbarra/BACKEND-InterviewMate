package com.interviewmate.InterviewMate.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.InterviewMate.config.AiServiceProperties;
import com.interviewmate.InterviewMate.dto.StartStudyRequest;
import com.interviewmate.InterviewMate.dto.StudySessionResponse;
import com.interviewmate.InterviewMate.dto.StudySessionSummaryResponse;
import com.interviewmate.InterviewMate.entity.InterviewTemplate;
import com.interviewmate.InterviewMate.entity.StudyQuestion;
import com.interviewmate.InterviewMate.entity.StudySession;
import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.enums.StudyDifficulty;
import com.interviewmate.InterviewMate.enums.StudyQuestionType;
import com.interviewmate.InterviewMate.exception.BadRequestException;
import com.interviewmate.InterviewMate.exception.EntityNotFoundException;
import com.interviewmate.InterviewMate.mapper.StudyMapper;
import com.interviewmate.InterviewMate.repository.InterviewTemplateRepository;
import com.interviewmate.InterviewMate.repository.StudyQuestionRepository;
import com.interviewmate.InterviewMate.repository.StudySessionRepository;
import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.service.StudyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudyServiceImpl implements StudyService {

    private static final Logger log = LoggerFactory.getLogger(StudyServiceImpl.class);

    private final StudySessionRepository studySessionRepository;
    private final StudyQuestionRepository studyQuestionRepository;
    private final InterviewTemplateRepository templateRepository;
    private final StudyMapper studyMapper;
    private final UserRepository userRepository;
    private final AiServiceProperties aiServiceProperties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public StudyServiceImpl(StudySessionRepository studySessionRepository,
                            StudyQuestionRepository studyQuestionRepository,
                            InterviewTemplateRepository templateRepository,
                            StudyMapper studyMapper,
                            UserRepository userRepository,
                            AiServiceProperties aiServiceProperties,
                            ObjectMapper objectMapper) {
        this.studySessionRepository = studySessionRepository;
        this.studyQuestionRepository = studyQuestionRepository;
        this.templateRepository = templateRepository;
        this.studyMapper = studyMapper;
        this.userRepository = userRepository;
        this.aiServiceProperties = aiServiceProperties;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Transactional
    public StudySessionResponse start(StartStudyRequest request) {
        User authenticatedUser = getAuthenticatedUser();
        InterviewTemplate template = findTemplateOwnedByUser(request.getTemplateId(), authenticatedUser.getId());
        String topic = resolveTopic(request, template);

        StudySession session = new StudySession();
        session.setUser(authenticatedUser);
        session.setTemplate(template);
        session.setTopic(topic);

        StudySession savedSession = studySessionRepository.save(session);
        List<StudyQuestion> questions;
        if (isStudyAiEnabled()) {
            questions = buildAiQuestions(savedSession);
            log.info("AI ENABLED -> Generated study questions with provider={} model={}",
                    aiServiceProperties.getStudy().getProvider(),
                    aiServiceProperties.getStudy().getTextModel());
        } else {
            log.warn("AI FALLBACK -> Study AI disabled while generating questions (reason={})", studyAiDisabledReason());
            questions = buildFallbackQuestions(savedSession);
        }
        studyQuestionRepository.saveAll(questions);

        List<StudyQuestion> persistedQuestions = studyQuestionRepository.findByStudySessionIdOrderByOrderIndex(savedSession.getId());
        return studyMapper.toSessionResponse(savedSession, persistedQuestions);
    }

    @Override
    public StudySessionResponse getById(UUID studySessionId) {
        StudySession session = findStudySessionOrThrow(studySessionId);
        verifyOwnership(session);

        List<StudyQuestion> questions = studyQuestionRepository.findByStudySessionIdOrderByOrderIndex(studySessionId);
        return studyMapper.toSessionResponse(session, questions);
    }

    @Override
    public List<StudySessionSummaryResponse> getByAuthenticatedUser() {
        User user = getAuthenticatedUser();
        return studySessionRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(session -> StudySessionSummaryResponse.builder()
                        .id(session.getId())
                        .templateId(session.getTemplate() == null ? null : session.getTemplate().getId())
                        .topic(session.getTopic())
                        .questionCount(studySessionRepository.countQuestionsBySessionId(session.getId()))
                        .createdAt(session.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public String transcribeAudio(MultipartFile audioFile) {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new BadRequestException("Audio file is required");
        }
        if (isGeminiProvider(aiServiceProperties.getStudy().getProvider())) {
            throw new UnsupportedOperationException("Audio transcription is not available with provider google-gemini in this module.");
        }
        if (!isStudyAiEnabled()) {
            log.warn("AI FALLBACK -> Study transcription blocked because AI is disabled (reason={})", studyAiDisabledReason());
            throw new IllegalStateException("Study AI is disabled; cannot transcribe audio.");
        }

        String filename = audioFile.getOriginalFilename() == null ? "audio.wav" : audioFile.getOriginalFilename();
        String url = normalizedBaseUrl(aiServiceProperties.getStudy().getBaseUrl()) + "/audio/transcriptions";

        try {
            ByteArrayResource fileResource = new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(aiServiceProperties.getStudy().getApiKey());
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            body.add("model", aiServiceProperties.getStudy().getTranscriptionModel());

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    new ParameterizedTypeReference<>() {}
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalStateException("Failed to transcribe audio");
            }

            Object text = response.getBody().get("text");
            return text == null ? "" : String.valueOf(text).trim();
        } catch (Exception ex) {
            log.warn("AI FALLBACK -> Failed to transcribe audio with AI: {}", ex.getMessage());
            throw new IllegalStateException("Failed to transcribe audio", ex);
        }
    }

    private StudySession findStudySessionOrThrow(UUID studySessionId) {
        return studySessionRepository.findById(studySessionId)
                .orElseThrow(() -> new EntityNotFoundException("Study session not found: " + studySessionId));
    }

    private InterviewTemplate findTemplateOwnedByUser(UUID templateId, Long userId) {
        if (templateId == null) {
            throw new BadRequestException("Template ID is required to start a study session");
        }
        InterviewTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template not found: " + templateId));
        if (!template.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not the owner of this interview template");
        }
        return template;
    }

    private String resolveTopic(StartStudyRequest request, InterviewTemplate template) {
        String rawTopic = request.getTopic() == null ? "" : request.getTopic().trim();

        if (!rawTopic.isEmpty()) {
            return rawTopic;
        }

        String position = template.getPosition() == null ? "Technical role" : template.getPosition().trim();
        String area = template.getWorkingArea() == null ? "general engineering" : template.getWorkingArea().trim();
        return position + " - " + area;
    }

    private List<StudyQuestion> buildFallbackQuestions(StudySession session) {
        String topic = session.getTopic();
        List<StudyQuestion> questions = new ArrayList<>();

        questions.add(buildQuestion(session, 1, "Explain the fundamentals of " + topic, StudyDifficulty.BASIC, StudyQuestionType.THEORETICAL));
        questions.add(buildQuestion(session, 2, "Describe a practical use case of " + topic, StudyDifficulty.BASIC, StudyQuestionType.PRACTICAL));
        questions.add(buildQuestion(session, 3, "What are common pitfalls when working with " + topic + "?", StudyDifficulty.INTERMEDIATE, StudyQuestionType.THEORETICAL));
        questions.add(buildQuestion(session, 4, "Design a small solution using " + topic, StudyDifficulty.INTERMEDIATE, StudyQuestionType.PRACTICAL));
        questions.add(buildQuestion(session, 5, "Compare trade-offs for advanced decisions in " + topic, StudyDifficulty.ADVANCED, StudyQuestionType.THEORETICAL));
        questions.add(buildQuestion(session, 6, "Propose a production-ready strategy for " + topic, StudyDifficulty.ADVANCED, StudyQuestionType.PRACTICAL));

        return questions;
    }

    private List<StudyQuestion> buildAiQuestions(StudySession session) {
        String systemPrompt = "You are an expert study coach. Return strict JSON array with exactly 6 objects. Each object keys: questionText, difficulty(BASIC|INTERMEDIATE|ADVANCED), type(THEORETICAL|PRACTICAL).";
        String userPrompt = buildStudyPrompt(session);

        try {
            ChatResult chat = callStudyChatCompletion(systemPrompt, userPrompt, 0.6, 700);
            List<Map<String, String>> generated = objectMapper.readValue(sanitizeJsonContent(chat.content()), new TypeReference<>() {});
            List<StudyQuestion> questions = new ArrayList<>();
            for (int i = 0; i < generated.size(); i++) {
                Map<String, String> row = generated.get(i);
                String text = row.getOrDefault("questionText", "Explain key concepts about " + session.getTopic());
                StudyDifficulty difficulty = parseDifficulty(row.get("difficulty"));
                StudyQuestionType type = parseType(row.get("type"));
                questions.add(buildQuestion(session, i + 1, text, difficulty, type));
            }
            if (questions.isEmpty()) {
                log.warn("AI FALLBACK -> Study AI returned empty questions, using fallback set.");
                return buildFallbackQuestions(session);
            }
            return questions;
        } catch (Exception ex) {
            log.warn("AI FALLBACK -> Failed generating study questions with AI: {}", ex.getMessage());
            return buildFallbackQuestions(session);
        }
    }

    private StudyQuestion buildQuestion(StudySession session,
                                        int orderIndex,
                                        String questionText,
                                        StudyDifficulty difficulty,
                                        StudyQuestionType type) {
        StudyQuestion question = new StudyQuestion();
        question.setStudySession(session);
        question.setOrderIndex(orderIndex);
        question.setQuestionText(questionText);
        question.setDifficulty(difficulty);
        question.setType(type);
        return question;
    }

    private String buildStudyPrompt(StudySession session) {
        InterviewTemplate template = session.getTemplate();
        if (template == null) {
            return "Generate study questions for topic: " + session.getTopic();
        }
        return "Generate study questions for this interview preparation profile: " +
                "enterprise=" + nullSafe(template.getEnterprise()) + ", " +
                "position=" + nullSafe(template.getPosition()) + ", " +
                "type=" + template.getType() + ", " +
                "workingArea=" + nullSafe(template.getWorkingArea()) + ", " +
                "requirements=" + nullSafe(template.getRequirements()) + ", " +
                "topic=" + nullSafe(session.getTopic()) + ".";
    }

    private ChatResult callStudyChatCompletion(String systemPrompt,
                                               String userPrompt,
                                               double temperature,
                                               int maxTokens) {
        if (isGeminiProvider(aiServiceProperties.getStudy().getProvider())) {
            return callGeminiStudyChatCompletion(systemPrompt, userPrompt, temperature, maxTokens);
        }

        String url = normalizedBaseUrl(aiServiceProperties.getStudy().getBaseUrl()) + "/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String apiKey = aiServiceProperties.getStudy().getApiKey();
        if (apiKey != null && !apiKey.isBlank()) {
            headers.setBearerAuth(apiKey);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", aiServiceProperties.getStudy().getTextModel());
        body.put("temperature", temperature);
        body.put("max_tokens", maxTokens);
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<>() {}
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Study chat completion failed");
        }

        Map<String, Object> payload = response.getBody();
        List<Map<String, Object>> choices = asListOfMaps(payload.get("choices"));
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("Study chat completion returned no choices");
        }
        Map<String, Object> message = asMap(choices.get(0).get("message"));
        String content = message == null ? "" : String.valueOf(message.getOrDefault("content", ""));

        int totalTokens = 0;
        Map<String, Object> usage = asMap(payload.get("usage"));
        if (usage != null && usage.get("total_tokens") != null) {
            totalTokens = parseTotalTokens(usage.get("total_tokens"));
        }
        return new ChatResult(content, totalTokens);
    }

    private ChatResult callGeminiStudyChatCompletion(String systemPrompt,
                                                     String userPrompt,
                                                     double temperature,
                                                     int maxTokens) {
        String model = aiServiceProperties.getStudy().getTextModel();
        String baseUrl = normalizedBaseUrl(aiServiceProperties.getStudy().getBaseUrl());
        String apiKey = aiServiceProperties.getStudy().getApiKey();

        String url = baseUrl + "/models/" + model + ":generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemPrompt))));
        body.put("contents", List.of(Map.of("parts", List.of(Map.of("text", userPrompt)))));
        body.put("generationConfig", Map.of(
                "temperature", temperature,
                "maxOutputTokens", maxTokens
        ));

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<>() {}
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Gemini study generateContent failed");
        }

        Map<String, Object> payload = response.getBody();
        List<Map<String, Object>> candidates = asListOfMaps(payload.get("candidates"));
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException("Gemini study returned no candidates");
        }

        Map<String, Object> firstCandidate = asMap(candidates.get(0));
        Map<String, Object> content = firstCandidate == null ? null : asMap(firstCandidate.get("content"));
        List<Map<String, Object>> parts = content == null ? null : asListOfMaps(content.get("parts"));
        String text = "";
        if (parts != null && !parts.isEmpty()) {
            text = String.valueOf(parts.get(0).getOrDefault("text", ""));
        }

        int totalTokens = 0;
        Map<String, Object> usageMetadata = asMap(payload.get("usageMetadata"));
        if (usageMetadata != null && usageMetadata.get("totalTokenCount") != null) {
            totalTokens = parseTotalTokens(usageMetadata.get("totalTokenCount"));
        }

        return new ChatResult(text, totalTokens);
    }

    private String sanitizeJsonContent(String content) {
        if (content == null) {
            return "[]";
        }
        String trimmed = content.trim();
        if (trimmed.startsWith("```") && trimmed.endsWith("```")) {
            String withoutFence = trimmed.substring(3, trimmed.length() - 3).trim();
            if (withoutFence.startsWith("json")) {
                return withoutFence.substring(4).trim();
            }
            return withoutFence;
        }
        return trimmed;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> asListOfMaps(Object value) {
        if (value instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return null;
    }

    private int parseTotalTokens(Object raw) {
        try {
            return Integer.parseInt(String.valueOf(raw));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private StudyDifficulty parseDifficulty(String raw) {
        try {
            return StudyDifficulty.valueOf(raw == null ? "" : raw.trim().toUpperCase());
        } catch (Exception ignored) {
            return StudyDifficulty.INTERMEDIATE;
        }
    }

    private StudyQuestionType parseType(String raw) {
        try {
            return StudyQuestionType.valueOf(raw == null ? "" : raw.trim().toUpperCase());
        } catch (Exception ignored) {
            return StudyQuestionType.THEORETICAL;
        }
    }

    private boolean isStudyAiEnabled() {
        return aiServiceProperties.getStudy().isEnabled()
                && aiServiceProperties.getStudy().getApiKey() != null
                && !aiServiceProperties.getStudy().getApiKey().isBlank();
    }

    private String studyAiDisabledReason() {
        if (!aiServiceProperties.getStudy().isEnabled()) {
            return "enabledFlag=false";
        }
        if (aiServiceProperties.getStudy().getApiKey() == null || aiServiceProperties.getStudy().getApiKey().isBlank()) {
            return "apiKeyMissing";
        }
        return "unknown";
    }

    private String normalizedBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return isGeminiProvider(aiServiceProperties.getStudy().getProvider())
                    ? "https://generativelanguage.googleapis.com/v1beta"
                    : "https://api.openai.com/v1";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private boolean isGeminiProvider(String provider) {
        return provider != null && provider.trim().equalsIgnoreCase("google-gemini");
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
    }

    private void verifyOwnership(StudySession session) {
        User user = getAuthenticatedUser();
        if (!session.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the owner of this study session");
        }
    }

    private record ChatResult(String content, int totalTokens) {}
}

