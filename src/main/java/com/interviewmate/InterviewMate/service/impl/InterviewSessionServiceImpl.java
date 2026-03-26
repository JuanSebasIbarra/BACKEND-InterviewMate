package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.dto.CreateSessionRequest;
import com.interviewmate.InterviewMate.dto.SessionResponse;
import com.interviewmate.InterviewMate.entity.InterviewSession;
import com.interviewmate.InterviewMate.entity.InterviewTemplate;
import com.interviewmate.InterviewMate.enums.SessionStatus;
import com.interviewmate.InterviewMate.exception.EntityNotFoundException;
import com.interviewmate.InterviewMate.mapper.InterviewSessionMapper;
import com.interviewmate.InterviewMate.repository.InterviewSessionRepository;
import com.interviewmate.InterviewMate.repository.InterviewTemplateRepository;
import com.interviewmate.InterviewMate.service.AiInterviewService;
import com.interviewmate.InterviewMate.service.InterviewSessionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InterviewSessionServiceImpl implements InterviewSessionService {

    private final InterviewSessionRepository sessionRepository;
    private final InterviewTemplateRepository templateRepository;
    private final InterviewSessionMapper sessionMapper;
    private final AiInterviewService aiInterviewService;

    public InterviewSessionServiceImpl(InterviewSessionRepository sessionRepository,
                                       InterviewTemplateRepository templateRepository,
                                       InterviewSessionMapper sessionMapper,
                                       AiInterviewService aiInterviewService) {
        this.sessionRepository = sessionRepository;
        this.templateRepository = templateRepository;
        this.sessionMapper = sessionMapper;
        this.aiInterviewService = aiInterviewService;
    }

    @Override
    public SessionResponse create(CreateSessionRequest request) {
        InterviewTemplate template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new EntityNotFoundException("Template not found: " + request.getTemplateId()));
        int attemptNumber = sessionRepository.countByTemplateId(request.getTemplateId()) + 1;
        InterviewSession session = sessionMapper.toEntity(template, attemptNumber);
        return sessionMapper.toResponse(sessionRepository.save(session));
    }

    @Override
    public SessionResponse begin(UUID sessionId) {
        InterviewSession session = findOrThrow(sessionId);
        if (session.getStatus() != SessionStatus.PENDING) {
            throw new IllegalStateException("Session must be PENDING to begin, current status: " + session.getStatus());
        }
        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setStartedAt(LocalDateTime.now());
        session = sessionRepository.save(session);
        aiInterviewService.generateQuestionsForSession(sessionId);
        return sessionMapper.toResponse(session);
    }

    @Override
    public SessionResponse complete(UUID sessionId) {
        InterviewSession session = findOrThrow(sessionId);
        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Session must be IN_PROGRESS to complete, current status: " + session.getStatus());
        }
        session.setStatus(SessionStatus.COMPLETED);
        session.setCompletedAt(LocalDateTime.now());
        return sessionMapper.toResponse(sessionRepository.save(session));
    }

    @Override
    public SessionResponse getById(UUID sessionId) {
        return sessionMapper.toResponse(findOrThrow(sessionId));
    }

    @Override
    public List<SessionResponse> getAllByTemplate(UUID templateId) {
        return sessionRepository.findByTemplateId(templateId).stream()
                .map(sessionMapper::toResponse)
                .collect(Collectors.toList());
    }

    private InterviewSession findOrThrow(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + sessionId));
    }
}
