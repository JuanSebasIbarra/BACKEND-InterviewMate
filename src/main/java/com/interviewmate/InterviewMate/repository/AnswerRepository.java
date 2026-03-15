package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.Answer;
import com.interviewmate.InterviewMate.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestion(Question question);
    boolean existsByQuestion(Question question);
}

