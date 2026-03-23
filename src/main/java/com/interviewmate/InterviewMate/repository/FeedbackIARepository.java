package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.FeedbackIA;
import com.interviewmate.InterviewMate.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackIARepository extends JpaRepository<FeedbackIA, Long> {
    Optional<FeedbackIA> findByAnswer(Answer answer);
}

