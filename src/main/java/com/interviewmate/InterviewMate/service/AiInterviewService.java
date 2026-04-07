package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.dto.EvaluationResult;

import java.util.UUID;

public interface AiInterviewService {
    void generateQuestionsForSession(UUID sessionId);
    void evaluateAnswer(UUID questionId);
    EvaluationResult evaluateResponse(String question, String userResponse);
    void generateResult(UUID sessionId);
    byte[] generateSpeechForQuestion(UUID questionId);
}
