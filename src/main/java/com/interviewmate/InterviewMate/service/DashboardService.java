package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.dto.DashboardStatsResponse;

public interface DashboardService {
    DashboardStatsResponse getStatsForAuthenticatedUser();
}

