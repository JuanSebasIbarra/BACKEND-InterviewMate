package com.ibarra.castro.maya.interviewmate.service;

import com.ibarra.castro.maya.interviewmate.dto.UserRequest;
import com.ibarra.castro.maya.interviewmate.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    Page<UserResponse> listUsers(Pageable pageable);
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
}
