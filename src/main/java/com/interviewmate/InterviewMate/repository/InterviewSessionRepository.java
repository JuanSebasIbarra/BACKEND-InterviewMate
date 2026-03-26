package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {
}
