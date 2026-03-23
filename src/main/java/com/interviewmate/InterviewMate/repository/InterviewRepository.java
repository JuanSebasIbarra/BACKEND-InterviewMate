package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.Interview;
import com.interviewmate.InterviewMate.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    Page<Interview> findByUser(User user, Pageable pageable);
    Page<Interview> findByUserAndStatus(User user, Interview.InterviewStatus status, Pageable pageable);
}

