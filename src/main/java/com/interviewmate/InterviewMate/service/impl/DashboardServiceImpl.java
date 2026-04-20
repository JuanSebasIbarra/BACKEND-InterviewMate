package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.dto.DashboardStatsResponse;
import com.interviewmate.InterviewMate.entity.InterviewResult;
import com.interviewmate.InterviewMate.entity.StudySession;
import com.interviewmate.InterviewMate.repository.InterviewResultRepository;
import com.interviewmate.InterviewMate.repository.InterviewTemplateRepository;
import com.interviewmate.InterviewMate.repository.StudySessionRepository;
import com.interviewmate.InterviewMate.service.AccessControlService;
import com.interviewmate.InterviewMate.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final AccessControlService accessControlService;
    private final InterviewResultRepository resultRepository;
    private final StudySessionRepository studySessionRepository;
    private final InterviewTemplateRepository templateRepository;

    public DashboardServiceImpl(AccessControlService accessControlService,
                                InterviewResultRepository resultRepository,
                                StudySessionRepository studySessionRepository,
                                InterviewTemplateRepository templateRepository) {
        this.accessControlService = accessControlService;
        this.resultRepository = resultRepository;
        this.studySessionRepository = studySessionRepository;
        this.templateRepository = templateRepository;
    }

    @Override
    public DashboardStatsResponse getStatsForAuthenticatedUser() {
        Long userId = accessControlService.getAuthenticatedUser().getId();

        List<InterviewResult> results = resultRepository.findBySessionTemplateUserId(userId);

        long completedSessions = results.size();

        double avgScore = results.stream()
                .filter(r -> r.getTotalScore() != null)
                .mapToDouble(InterviewResult::getTotalScore)
                .average()
                .orElse(0.0);

        LocalDateTime lastInterviewDate = results.stream()
                .map(r -> r.getSession().getStartedAt())
                .filter(d -> d != null)
                .max(Comparator.naturalOrder())
                .orElse(null);

        List<StudySession> studySessions = studySessionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        long totalStudySessions = studySessions.size();

        LocalDateTime lastStudyDate = studySessions.stream()
                .map(StudySession::getCreatedAt)
                .filter(d -> d != null)
                .findFirst()
                .orElse(null);

        long totalTemplates = templateRepository.countByUserId(userId);

        return DashboardStatsResponse.builder()
                .totalInterviewSessionsCompleted(completedSessions)
                .avgInterviewScore(Math.round(avgScore * 10.0) / 10.0)
                .totalStudySessions(totalStudySessions)
                .totalInterviewTemplates(totalTemplates)
                .lastInterviewSessionDate(lastInterviewDate)
                .lastStudySessionDate(lastStudyDate)
                .build();
    }
}

