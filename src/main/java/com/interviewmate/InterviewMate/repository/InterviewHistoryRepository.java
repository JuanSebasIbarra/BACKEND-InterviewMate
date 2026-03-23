package com.interviewmate.InterviewMate.repository;

import com.interviewmate.InterviewMate.entity.InterviewHistory;
import com.interviewmate.InterviewMate.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewHistoryRepository extends JpaRepository<InterviewHistory, Long> {
    Page<InterviewHistory> findByUserOrderByTimestampDesc(User user, Pageable pageable);
}

