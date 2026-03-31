package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.StudyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StudyQuestionRepository extends JpaRepository<StudyQuestion, UUID> {
    List<StudyQuestion> findByStudySessionIdOrderByOrderIndex(UUID studySessionId);
    void deleteByStudySessionId(UUID studySessionId);
}

