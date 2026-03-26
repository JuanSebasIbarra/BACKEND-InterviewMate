package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.InterviewResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewResultRepository extends JpaRepository<InterviewResult, UUID> {
    Optional<InterviewResult> findBySessionId(UUID sessionId);
    List<InterviewResult> findBySessionTemplateUserId(Long userId);
}
