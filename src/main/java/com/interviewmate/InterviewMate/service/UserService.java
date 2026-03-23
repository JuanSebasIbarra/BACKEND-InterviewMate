package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.dto.ProfileRequest;
import com.interviewmate.InterviewMate.dto.ProfileResponse;
import com.interviewmate.InterviewMate.dto.UserRequest;
import com.interviewmate.InterviewMate.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    Page<UserResponse> listUsers(Pageable pageable);
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);

    ProfileResponse getProfile(String username);
    ProfileResponse updateProfile(String username, ProfileRequest request);
}
