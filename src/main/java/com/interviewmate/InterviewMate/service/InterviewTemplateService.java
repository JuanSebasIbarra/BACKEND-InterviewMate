package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.dto.CreateInterviewTemplateRequest;
import com.interviewmate.InterviewMate.dto.InterviewTemplateResponse;
import com.interviewmate.InterviewMate.dto.UpdateInterviewTemplateRequest;
import com.interviewmate.InterviewMate.enums.InterviewStatus;

import java.util.List;
import java.util.UUID;

public interface InterviewTemplateService {
    InterviewTemplateResponse create(CreateInterviewTemplateRequest request);
    InterviewTemplateResponse update(UUID templateId, UpdateInterviewTemplateRequest request);
    InterviewTemplateResponse changeStatus(UUID templateId, InterviewStatus newStatus);
    InterviewTemplateResponse getById(UUID templateId);
    List<InterviewTemplateResponse> getAllByAuthenticatedUser();
    void delete(UUID templateId);
}
