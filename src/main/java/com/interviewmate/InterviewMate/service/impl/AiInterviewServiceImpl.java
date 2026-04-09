package com.interviewmate.InterviewMate.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.InterviewMate.config.AiServiceProperties;
import com.interviewmate.InterviewMate.dto.CreateResultRequest;
import com.interviewmate.InterviewMate.dto.EvaluationResult;
import com.interviewmate.InterviewMate.entity.InterviewQuestion;
import com.interviewmate.InterviewMate.entity.InterviewSession;
import com.interviewmate.InterviewMate.exception.EntityNotFoundException;
import com.interviewmate.InterviewMate.mapper.InterviewQuestionMapper;
import com.interviewmate.InterviewMate.repository.InterviewQuestionRepository;
import com.interviewmate.InterviewMate.repository.InterviewSessionRepository;
import com.interviewmate.InterviewMate.service.AiInterviewService;
import com.interviewmate.InterviewMate.service.InterviewResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AiInterviewServiceImpl implements AiInterviewService {

    private static final Logger log = LoggerFactory.getLogger(AiInterviewServiceImpl.class);

    private final InterviewQuestionRepository questionRepository;
    private final InterviewSessionRepository sessionRepository;
    private final InterviewResultService resultService;
    private final InterviewQuestionMapper questionMapper;
    private final AiServiceProperties aiServiceProperties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public AiInterviewServiceImpl(InterviewQuestionRepository questionRepository,
                                  InterviewSessionRepository sessionRepository,
                                  InterviewResultService resultService,
                                  InterviewQuestionMapper questionMapper,
                                  AiServiceProperties aiServiceProperties,
                                  ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.sessionRepository = sessionRepository;
        this.resultService = resultService;
        this.questionMapper = questionMapper;
        this.aiServiceProperties = aiServiceProperties;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void generateQuestionsForSession(UUID sessionId) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + sessionId));
        String model = aiServiceProperties.getInterview().getChatModel();

        List<String> generatedQuestions;
        if (isInterviewAiEnabled()) {
            String systemPrompt = "You are an expert technical interviewer. Return strict JSON array with exactly 5 strings.";
            String userPrompt = buildQuestionGenerationPrompt(session);
            try {
                ChatResult chat = callChatCompletion(systemPrompt, userPrompt, 0.7, 700);
                generatedQuestions = extractStringList(chat.content());
                log.info("AI ENABLED -> Generated interview questions with provider={} model={}",
                        aiServiceProperties.getInterview().getProvider(),
                        aiServiceProperties.getInterview().getChatModel());
            } catch (Exception ex) {
                log.warn("AI FALLBACK -> Failed generating interview questions with AI: {}", ex.getMessage());
                generatedQuestions = buildFallbackQuestions(session);
            }
        } else {
            log.warn("AI FALLBACK -> Interview AI disabled while generating questions (reason={})", interviewAiDisabledReason());
            generatedQuestions = buildFallbackQuestions(session);
        }

        if (generatedQuestions.isEmpty()) {
            generatedQuestions = buildFallbackQuestions(session);
        }

        questionRepository.deleteBySessionId(sessionId);
        List<InterviewQuestion> questions = new ArrayList<>();
        for (int i = 0; i < generatedQuestions.size(); i++) {
            questions.add(questionMapper.toEntity(session, generatedQuestions.get(i), i + 1, model));
        }
        questionRepository.saveAll(questions);
    }

    @Override
    public void evaluateAnswer(UUID questionId) {
        InterviewQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found: " + questionId));

        if (question.getAnswer() == null || question.getAnswer().isBlank()) {
            question.setAiFeedback("No evaluable answer was provided.");
            question.setScore(0.0);
            question.setAiModel(aiServiceProperties.getInterview().getChatModel());
            questionRepository.save(question);
            return;
        }

        try {
            EvaluationResult result = evaluateResponse(question.getQuestion(), question.getAnswer());
            question.setAiFeedback(buildStoredFeedback(result));
            question.setScore((double) result.score());
        } catch (Exception ex) {
            log.warn("AI FALLBACK -> Failed evaluating answer with AI: {}", ex.getMessage());
            question.setAiFeedback("Could not evaluate with AI, using fallback evaluation.");
            question.setScore(70.0);
        }
        question.setAiModel(aiServiceProperties.getInterview().getChatModel());
        questionRepository.save(question);
    }

    @Override
    public EvaluationResult evaluateResponse(String question, String userResponse) {
        if (question == null || question.isBlank() || userResponse == null || userResponse.isBlank()) {
            return fallbackEvaluationResult();
        }
        if (!isInterviewAiEnabled()) {
            log.warn("AI FALLBACK -> Interview AI disabled while evaluating answer (reason={})", interviewAiDisabledReason());
            return fallbackEvaluationResult();
        }

        String systemPrompt = """
                Act as a senior technical interviewer.
                Analyze the candidate response and return strict JSON with keys:
                score (0..100), strengths (array of strings), codeSmells (array of strings),
                technicalFeedback (string), suggestedImprovement (string).
                Return JSON only.
                """;
        String userPrompt = "Question: " + question + "\nCandidate answer: " + userResponse;

        try {
            ChatResult chat = callChatCompletion(systemPrompt, userPrompt, 0.2, 700);
            log.info("AI ENABLED -> Evaluated interview answer with model={}", aiServiceProperties.getInterview().getChatModel());
            return parseEvaluationResult(chat.content());
        } catch (Exception ex) {
            log.warn("AI FALLBACK -> evaluateResponse failed with AI: {}", ex.getMessage());
            return fallbackEvaluationResult();
        }
    }

    @Override
    public void generateResult(UUID sessionId) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + sessionId));
        List<InterviewQuestion> questions = questionRepository.findBySessionIdOrderByOrderIndex(sessionId);

        double avgScore = questions.stream()
                .filter(q -> q.getScore() != null)
                .mapToDouble(InterviewQuestion::getScore)
                .average()
                .orElse(0.0);

        CreateResultRequest request = new CreateResultRequest();
        request.setTotalScore(avgScore);
        request.setAiModel(aiServiceProperties.getInterview().getChatModel());

        if (isInterviewAiEnabled()) {
            String qaSummary = questions.stream()
                    .map(q -> "Q: " + q.getQuestion() + " | Score: " + (q.getScore() == null ? "N/A" : q.getScore()) + " | Feedback: " + nullSafe(q.getAiFeedback()))
                    .collect(Collectors.joining("\n"));
            String systemPrompt = "You summarize interview performance. Return strict JSON with keys: generalFeedback, strengths, weaknesses.";
            String userPrompt = "Candidate interview summary:\n" + qaSummary + "\nAverage score: " + avgScore;
            try {
                ChatResult chat = callChatCompletion(systemPrompt, userPrompt, 0.3, 700);
                Map<String, Object> payload = objectMapper.readValue(sanitizeJsonContent(chat.content()), new TypeReference<>() {});
                request.setGeneralFeedback(String.valueOf(payload.getOrDefault("generalFeedback", "Interview completed successfully.")));
                request.setStrengths(String.valueOf(payload.getOrDefault("strengths", "Good baseline communication and structured answers.")));
                request.setWeaknesses(String.valueOf(payload.getOrDefault("weaknesses", "Need more depth and concrete technical examples.")));
                request.setTotalTokensUsed(chat.totalTokens());
                log.info("AI ENABLED -> Generated interview result summary with model={}", aiServiceProperties.getInterview().getChatModel());
            } catch (Exception ex) {
                log.warn("AI FALLBACK -> Failed generating result summary with AI: {}", ex.getMessage());
                setFallbackResultTexts(request);
                request.setTotalTokensUsed(0);
            }
        } else {
            log.warn("AI FALLBACK -> Interview AI disabled while generating result (reason={})", interviewAiDisabledReason());
            setFallbackResultTexts(request);
            request.setTotalTokensUsed(0);
        }

        resultService.save(request, session);
    }

    @Override
    public byte[] generateSpeechForQuestion(UUID questionId) {
        if (isGeminiProvider(aiServiceProperties.getInterview().getProvider())) {
            throw new UnsupportedOperationException("Text-to-speech is not available with provider google-gemini in this module.");
        }
        throw new UnsupportedOperationException("Text-to-speech is not implemented for the current provider.");
    }

    private ChatResult callChatCompletion(String systemPrompt,
                                          String userPrompt,
                                          double temperature,
                                          int maxTokens) {
        if (isGeminiProvider(aiServiceProperties.getInterview().getProvider())) {
            return callGeminiChatCompletion(systemPrompt, userPrompt, temperature, maxTokens);
        }

        String url = normalizedBaseUrl(aiServiceProperties.getInterview().getBaseUrl()) + "/chat/completions";
        HttpHeaders headers = buildJsonHeaders(aiServiceProperties.getInterview().getApiKey());

        Map<String, Object> body = new HashMap<>();
        body.put("model", aiServiceProperties.getInterview().getChatModel());
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
            throw new IllegalStateException("Chat completion request failed");
        }

        Map<String, Object> payload = response.getBody();
        List<Map<String, Object>> choices = asListOfMaps(payload.get("choices"));
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("Chat completion returned no choices");
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

    private ChatResult callGeminiChatCompletion(String systemPrompt,
                                                String userPrompt,
                                                double temperature,
                                                int maxTokens) {
        String model = aiServiceProperties.getInterview().getChatModel();
        String baseUrl = normalizedBaseUrl(aiServiceProperties.getInterview().getBaseUrl());
        String apiKey = aiServiceProperties.getInterview().getApiKey();
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
            throw new IllegalStateException("Gemini generateContent request failed");
        }

        Map<String, Object> payload = response.getBody();
        List<Map<String, Object>> candidates = asListOfMaps(payload.get("candidates"));
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException("Gemini returned no candidates");
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

    private HttpHeaders buildJsonHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isBlank()) {
            headers.setBearerAuth(apiKey);
        }
        return headers;
    }

    private boolean isInterviewAiEnabled() {
        return aiServiceProperties.getInterview().isEnabled()
                && aiServiceProperties.getInterview().getApiKey() != null
                && !aiServiceProperties.getInterview().getApiKey().isBlank();
    }

    private String interviewAiDisabledReason() {
        if (!aiServiceProperties.getInterview().isEnabled()) {
            return "enabledFlag=false";
        }
        if (aiServiceProperties.getInterview().getApiKey() == null || aiServiceProperties.getInterview().getApiKey().isBlank()) {
            return "apiKeyMissing";
        }
        return "unknown";
    }

    private String normalizedBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return isGeminiProvider(aiServiceProperties.getInterview().getProvider())
                    ? "https://generativelanguage.googleapis.com/v1beta"
                    : "https://api.openai.com/v1";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private boolean isGeminiProvider(String provider) {
        return provider != null && provider.trim().equalsIgnoreCase("google-gemini");
    }

    private List<String> extractStringList(String content) throws Exception {
        List<String> list = objectMapper.readValue(sanitizeJsonContent(content), new TypeReference<>() {});
        return list.stream().map(String::trim).filter(s -> !s.isBlank()).toList();
    }

    private String buildQuestionGenerationPrompt(InterviewSession session) {
        if (session.getTemplate() == null) {
            return "Generate 5 interview questions for a generic technical profile.";
        }
        return "Generate 5 interview questions for this profile: " +
                "enterprise=" + nullSafe(session.getTemplate().getEnterprise()) + ", " +
                "position=" + nullSafe(session.getTemplate().getPosition()) + ", " +
                "type=" + session.getTemplate().getType() + ", " +
                "workingArea=" + nullSafe(session.getTemplate().getWorkingArea()) + ", " +
                "requirements=" + nullSafe(session.getTemplate().getRequirements()) + ".";
    }

    private List<String> buildFallbackQuestions(InterviewSession session) {
        String position = session.getTemplate() == null ? "Generic technical role" : nullSafe(session.getTemplate().getPosition());
        List<String> fallback = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            fallback.add("Question " + i + " for position: " + position);
        }
        return fallback;
    }

    private void setFallbackResultTexts(CreateResultRequest request) {
        request.setGeneralFeedback("Interview completed. Keep practicing concise and structured responses.");
        request.setStrengths("Clear communication and consistent participation across the session.");
        request.setWeaknesses("Add more concrete examples, metrics, and technical depth in key answers.");
    }

    private double parseScore(Object raw) {
        if (raw == null) {
            return 70.0;
        }
        try {
            double parsed = Double.parseDouble(String.valueOf(raw));
            return Math.max(0.0, Math.min(100.0, parsed));
        } catch (NumberFormatException ex) {
            return 70.0;
        }
    }

    private EvaluationResult parseEvaluationResult(String content) throws Exception {
        Map<String, Object> payload = objectMapper.readValue(sanitizeJsonContent(content), new TypeReference<>() {});
        int score = (int) Math.round(parseScore(payload.get("score")));
        List<String> strengths = parseStringList(payload.get("strengths"), List.of("Structured communication"));
        List<String> codeSmells = parseStringList(payload.get("codeSmells"), List.of("No major code smells identified or not enough code context"));
        String technicalFeedback = String.valueOf(payload.getOrDefault(
                "technicalFeedback",
                "The answer is reasonable but needs more technical depth and precise trade-off analysis."
        ));
        String suggestedImprovement = String.valueOf(payload.getOrDefault(
                "suggestedImprovement",
                "Include concrete examples, edge cases, and implementation-level details."
        ));
        return new EvaluationResult(score, strengths, codeSmells, technicalFeedback, suggestedImprovement);
    }

    private List<String> parseStringList(Object raw, List<String> fallback) {
        if (raw instanceof List<?> list) {
            List<String> parsed = list.stream()
                    .map(String::valueOf)
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
            if (!parsed.isEmpty()) {
                return parsed;
            }
        }
        return fallback;
    }

    private EvaluationResult fallbackEvaluationResult() {
        return new EvaluationResult(
                70,
                List.of("Structured baseline answer", "Clear attempt to address the question"),
                List.of("Lack of deeper technical detail"),
                "The answer shows a reasonable understanding, but it needs more technical depth and clearer justification.",
                "Include concrete examples, trade-offs, complexity considerations and implementation details."
        );
    }

    private String buildStoredFeedback(EvaluationResult result) {
        return result.technicalFeedback() + " Improvement: " + result.suggestedImprovement();
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

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private record ChatResult(String content, int totalTokens) {}
}

