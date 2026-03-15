package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.Question;
import com.interviewmate.InterviewMate.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByInterviewOrderByQuestionOrderAsc(Interview interview);
    boolean existsByInterviewAndQuestionOrder(Interview interview, Integer questionOrder);
}

