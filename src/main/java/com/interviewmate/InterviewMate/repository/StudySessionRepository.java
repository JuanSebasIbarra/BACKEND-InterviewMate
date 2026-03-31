package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StudySessionRepository extends JpaRepository<StudySession, UUID> {
    List<StudySession> findByUserIdOrderByCreatedAtDesc(Long userId);
}

