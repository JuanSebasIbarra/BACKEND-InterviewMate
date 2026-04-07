package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.dto.DashboardStatsResponse;
import com.interviewmate.InterviewMate.entity.InterviewResult;
import com.interviewmate.InterviewMate.entity.StudySession;
import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.exception.EntityNotFoundException;
import com.interviewmate.InterviewMate.repository.InterviewResultRepository;
import com.interviewmate.InterviewMate.repository.InterviewTemplateRepository;
import com.interviewmate.InterviewMate.repository.StudySessionRepository;
import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.service.DashboardService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final InterviewResultRepository resultRepository;
    private final StudySessionRepository studySessionRepository;
    private final InterviewTemplateRepository templateRepository;

    public DashboardServiceImpl(UserRepository userRepository,
                                InterviewResultRepository resultRepository,
                                StudySessionRepository studySessionRepository,
                                InterviewTemplateRepository templateRepository) {
        this.userRepository = userRepository;
        this.resultRepository = resultRepository;
        this.studySessionRepository = studySessionRepository;
        this.templateRepository = templateRepository;
    }

    @Override
    public DashboardStatsResponse getStatsForAuthenticatedUser() {
        User user = getAuthenticatedUser();
        Long userId = user.getId();

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

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
    }
}

