package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.dto.CreateResultRequest;
import com.interviewmate.InterviewMate.dto.InterviewResultResponse;
import com.interviewmate.InterviewMate.entity.InterviewSession;

import java.util.List;
import java.util.UUID;

public interface InterviewResultService {
    InterviewResultResponse getBySession(UUID sessionId);
    List<InterviewResultResponse> getByAuthenticatedUser();
    InterviewResultResponse save(CreateResultRequest request, InterviewSession session);
}
