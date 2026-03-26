package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.dto.QuestionResponse;
import com.interviewmate.InterviewMate.dto.SubmitAnswerRequest;

import java.util.List;
import java.util.UUID;

public interface InterviewQuestionService {
    List<QuestionResponse> getBySession(UUID sessionId);
    QuestionResponse getById(UUID questionId);
    QuestionResponse submitAnswer(UUID questionId, SubmitAnswerRequest request);
}
