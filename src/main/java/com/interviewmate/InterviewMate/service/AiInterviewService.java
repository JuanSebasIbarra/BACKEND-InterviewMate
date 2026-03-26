package com.interviewmate.InterviewMate.service;

import java.util.UUID;

public interface AiInterviewService {
    void generateQuestionsForSession(UUID sessionId);
    void evaluateAnswer(UUID questionId);
    void generateResult(UUID sessionId);
}
