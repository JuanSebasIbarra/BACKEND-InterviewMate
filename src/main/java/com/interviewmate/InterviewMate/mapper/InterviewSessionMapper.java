package com.interviewmate.InterviewMate.mapper;

import com.interviewmate.InterviewMate.dto.SessionResponse;
import com.interviewmate.InterviewMate.entity.InterviewSession;
import com.interviewmate.InterviewMate.entity.InterviewTemplate;
import org.springframework.stereotype.Component;

@Component
public class InterviewSessionMapper {

    public InterviewSession toEntity(InterviewTemplate template, int attemptNumber) {
        InterviewSession session = new InterviewSession();
        session.setTemplate(template);
        session.setAttemptNumber(attemptNumber);
        return session;
    }

    public SessionResponse toResponse(InterviewSession session) {
        SessionResponse response = new SessionResponse();
        response.setId(session.getId());
        response.setAttemptNumber(session.getAttemptNumber());
        response.setStatus(session.getStatus());
        response.setStartedAt(session.getStartedAt());
        response.setCompletedAt(session.getCompletedAt());
        response.setTemplateId(session.getTemplate().getId());
        response.setTemplatePosition(session.getTemplate().getPosition());
        response.setTemplateEnterprise(session.getTemplate().getEnterprise());
        return response;
    }
}
