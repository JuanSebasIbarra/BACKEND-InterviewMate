package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.dto.GenerateStudyQuestionsRequest;
import com.interviewmate.InterviewMate.dto.StartStudyRequest;
import com.interviewmate.InterviewMate.dto.StudySessionResponse;

import java.util.UUID;

public interface StudyService {
    StudySessionResponse start(StartStudyRequest request);
    StudySessionResponse generateQuestions(GenerateStudyQuestionsRequest request);
    StudySessionResponse getById(UUID studySessionId);
}

