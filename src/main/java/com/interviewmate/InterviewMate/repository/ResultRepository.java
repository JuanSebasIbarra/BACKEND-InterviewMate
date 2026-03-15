package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.Result;
import com.interviewmate.InterviewMate.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByInterview(Interview interview);
}

