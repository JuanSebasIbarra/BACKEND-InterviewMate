package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StudySessionRepository extends JpaRepository<StudySession, UUID> {
    List<StudySession> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COUNT(sq) FROM StudyQuestion sq WHERE sq.studySession.id = :sessionId")
    int countQuestionsBySessionId(@Param("sessionId") UUID sessionId);
}
