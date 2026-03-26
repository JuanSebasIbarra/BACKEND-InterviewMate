package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.InterviewTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterviewTemplateRepository extends JpaRepository<InterviewTemplate, UUID> {
    List<InterviewTemplate> findByUserId(Long userId);
}
