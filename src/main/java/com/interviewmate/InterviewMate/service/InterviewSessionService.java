package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.dto.CreateSessionRequest;
import com.interviewmate.InterviewMate.dto.SessionResponse;

import java.util.List;
import java.util.UUID;

public interface InterviewSessionService {
    SessionResponse create(CreateSessionRequest request);
    SessionResponse begin(UUID sessionId);
    SessionResponse complete(UUID sessionId);
    SessionResponse getById(UUID sessionId);
    List<SessionResponse> getAllByTemplate(UUID templateId);
}
