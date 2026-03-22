package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.dto.UserRequest;
import com.interviewmate.InterviewMate.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class AuthenticatedUserServiceDecorator implements UserService {

    @Qualifier("userServiceImpl")
    private final UserService userService;

    private void checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        // Create user might not require authentication, or perhaps only admins can create
        // For now, allow without auth, as per spec
        return userService.createUser(request);
    }

    @Override
    public UserResponse getUserById(Long id) {
        checkAuthentication();
        return userService.getUserById(id);
    }

    @Override
    public Page<UserResponse> listUsers(Pageable pageable) {
        checkAuthentication();
        return userService.listUsers(pageable);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        checkAuthentication();
        // Additional check: only allow updating own user or if admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        // Assuming we can get the user by id and check username
        UserResponse user = userService.getUserById(id);
        if (!user.getUsername().equals(currentUsername) && !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Cannot update other user's data");
        }
        return userService.updateUser(id, request);
    }

    @Override
    public void deleteUser(Long id) {
        checkAuthentication();
        // Similar check for delete
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        UserResponse user = userService.getUserById(id);
        if (!user.getUsername().equals(currentUsername) && !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Cannot delete other user's data");
        }
        userService.deleteUser(id);
    }
}
