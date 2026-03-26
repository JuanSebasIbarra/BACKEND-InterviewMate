package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.InterviewResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InterviewResultRepository extends JpaRepository<InterviewResult, UUID> {
}
