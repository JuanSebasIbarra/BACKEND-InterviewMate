package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.dto.AuthResponse;
import com.interviewmate.InterviewMate.dto.LoginRequest;
import com.interviewmate.InterviewMate.dto.RegisterRequest;

public interface AuthService {

    /**
     * Registers a new user, hashes their password, assigns ROLE_USER,
     * and returns a JWT token so they are immediately authenticated.
     *
     * @param request registration data
     * @return JWT response
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Validates credentials and returns a JWT token.
     *
     * @param request login credentials
     * @return JWT response
     */
    AuthResponse login(LoginRequest request);
}
