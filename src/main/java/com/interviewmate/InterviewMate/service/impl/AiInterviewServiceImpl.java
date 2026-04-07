package com.interviewmate.InterviewMate.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.InterviewMate.config.AiServiceProperties;
import com.interviewmate.InterviewMate.dto.CreateResultRequest;
import com.interviewmate.InterviewMate.entity.InterviewQuestion;
import com.interviewmate.InterviewMate.entity.InterviewSession;
import com.interviewmate.InterviewMate.exception.EntityNotFoundException;
import com.interviewmate.InterviewMate.mapper.InterviewQuestionMapper;
import com.interviewmate.InterviewMate.repository.InterviewQuestionRepository;
import com.interviewmate.InterviewMate.repository.InterviewSessionRepository;
import com.interviewmate.InterviewMate.service.AiInterviewService;
import com.interviewmate.InterviewMate.service.InterviewResultService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
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
            String systemPrompt = "You are an expert technical interviewer. Return only a JSON array of 5 strings.";
            String userPrompt = buildQuestionGenerationPrompt(session);
            try {
                ChatResult chat = callChatCompletion(systemPrompt, userPrompt, 0.7, 600);
                generatedQuestions = extractStringList(chat.content());
            } catch (Exception ex) {
                generatedQuestions = buildFallbackQuestions(session);
            }
        } else {
            generatedQuestions = buildFallbackQuestions(session);
        }

        questionRepository.deleteBySessionId(sessionId);
        List<InterviewQuestion> questions = new ArrayList<>();
        for (int i = 0; i < Math.min(generatedQuestions.size(), 5); i++) {
            InterviewQuestion question = questionMapper.toEntity(session, generatedQuestions.get(i), i + 1, model);
            questions.add(question);
        }
        if (questions.isEmpty()) {
            List<String> fallback = buildFallbackQuestions(session);
            for (int i = 0; i < fallback.size(); i++) {
                questions.add(questionMapper.toEntity(session, fallback.get(i), i + 1, model));
            }
        }
        questionRepository.saveAll(questions);
    }

    @Override
    public void evaluateAnswer(UUID questionId) {
        InterviewQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found: " + questionId));

        if (!isInterviewAiEnabled() || question.getAnswer() == null || question.getAnswer().isBlank()) {
            question.setAiFeedback("No evaluable answer was provided.");
            question.setScore(0.0);
            question.setAiModel(aiServiceProperties.getInterview().getChatModel());
            questionRepository.save(question);
            return;
        }

        String systemPrompt = "You evaluate interview answers. Return strict JSON with keys: score (0-100), feedback.";
        String userPrompt = "Question: " + question.getQuestion() + "\nCandidate answer: " + question.getAnswer();
        try {
            ChatResult chat = callChatCompletion(systemPrompt, userPrompt, 0.2, 400);
            Map<String, Object> payload = objectMapper.readValue(chat.content(), new TypeReference<>() {});
            double score = parseScore(payload.get("score"));
            String feedback = String.valueOf(payload.getOrDefault("feedback", "Good attempt. Keep refining your answer with concrete examples."));
            question.setAiFeedback(feedback);
            question.setScore(score);
        } catch (Exception ex) {
            question.setAiFeedback("Could not evaluate with AI, using fallback evaluation.");
            question.setScore(70.0);
        }
        question.setAiModel(aiServiceProperties.getInterview().getChatModel());
        questionRepository.save(question);
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
                ChatResult chat = callChatCompletion(systemPrompt, userPrompt, 0.3, 600);
                Map<String, Object> payload = objectMapper.readValue(chat.content(), new TypeReference<>() {});
                request.setGeneralFeedback(String.valueOf(payload.getOrDefault("generalFeedback", "Interview completed successfully.")));
                request.setStrengths(String.valueOf(payload.getOrDefault("strengths", "Good baseline communication and structured answers.")));
                request.setWeaknesses(String.valueOf(payload.getOrDefault("weaknesses", "Need more depth and concrete technical examples.")));
                request.setTotalTokensUsed(chat.totalTokens());
            } catch (Exception ex) {
                setFallbackResultTexts(request);
                request.setTotalTokensUsed(0);
            }
        } else {
            setFallbackResultTexts(request);
            request.setTotalTokensUsed(0);
        }

        resultService.save(request, session);
    }

    @Override
    public byte[] generateSpeechForQuestion(UUID questionId) {
        InterviewQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found: " + questionId));

        if (!isInterviewAiEnabled()) {
            throw new IllegalStateException("Interview AI is disabled; cannot generate TTS audio.");
        }

        String url = normalizedBaseUrl(aiServiceProperties.getInterview().getBaseUrl()) + "/audio/speech";
        HttpHeaders headers = buildJsonHeaders(aiServiceProperties.getInterview().getApiKey());
        Map<String, Object> body = new HashMap<>();
        body.put("model", aiServiceProperties.getInterview().getTtsModel());
        body.put("voice", aiServiceProperties.getInterview().getVoice());
        body.put("input", question.getQuestion());
        body.put("format", "mp3");

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                byte[].class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Failed to generate speech for question: " + questionId);
        }

        return response.getBody();
    }

    private ChatResult callChatCompletion(String systemPrompt,
                                          String userPrompt,
                                          double temperature,
                                          int maxTokens) {
        String url = normalizedBaseUrl(aiServiceProperties.getInterview().getBaseUrl()) + "/chat/completions";
        HttpHeaders headers = buildJsonHeaders(aiServiceProperties.getInterview().getApiKey());

        Map<String, Object> body = new HashMap<>();
        body.put("model", aiServiceProperties.getInterview().getChatModel());
        body.put("temperature", temperature);
        body.put("max_tokens", maxTokens);

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        );
        body.put("messages", messages);

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
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = asMap(firstChoice.get("message"));
        String content = message == null ? "" : String.valueOf(message.getOrDefault("content", ""));

        int totalTokens = 0;
        Map<String, Object> usage = asMap(payload.get("usage"));
        if (usage != null && usage.get("total_tokens") != null) {
            totalTokens = parseTotalTokens(usage.get("total_tokens"));
        }

        return new ChatResult(content, totalTokens);
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

    private String normalizedBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "https://api.openai.com/v1";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private List<String> extractStringList(String content) throws Exception {
        List<String> list = objectMapper.readValue(sanitizeJsonContent(content), new TypeReference<>() {});
        return list.stream().map(String::trim).filter(s -> !s.isBlank()).toList();
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
