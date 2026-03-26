package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, UUID> {
}
