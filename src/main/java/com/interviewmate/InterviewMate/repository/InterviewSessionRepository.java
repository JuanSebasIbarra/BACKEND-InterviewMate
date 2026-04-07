package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.InterviewSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {
    List<InterviewSession> findByTemplateId(UUID templateId);
    int countByTemplateId(UUID templateId);
    Page<InterviewSession> findByTemplateUserIdOrderByStartedAtDesc(Long userId, Pageable pageable);
}
