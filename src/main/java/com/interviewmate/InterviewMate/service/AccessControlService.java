package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.entity.InterviewQuestion;
import com.interviewmate.InterviewMate.entity.InterviewResult;
import com.interviewmate.InterviewMate.entity.InterviewSession;
import com.interviewmate.InterviewMate.entity.InterviewTemplate;
import com.interviewmate.InterviewMate.entity.StudySession;
import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.exception.EntityNotFoundException;
import com.interviewmate.InterviewMate.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AccessControlService {

    private final UserRepository userRepository;

    public AccessControlService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
    }

    // Method overloading: same operation for multiple domain types.
    public void assertOwnership(InterviewTemplate template) {
        assertSameOwner(template.getUser().getId(), "You are not the owner of this template");
    }

    public void assertOwnership(InterviewSession session) {
        assertSameOwner(session.getTemplate().getUser().getId(), "You are not the owner of this interview session");
    }

    public void assertOwnership(InterviewQuestion question) {
        assertSameOwner(question.getSession().getTemplate().getUser().getId(), "You are not the owner of this interview question");
    }

    public void assertOwnership(InterviewResult result) {
        assertSameOwner(result.getSession().getTemplate().getUser().getId(), "You are not the owner of this interview result");
    }

    public void assertOwnership(StudySession studySession) {
        assertSameOwner(studySession.getUser().getId(), "You are not the owner of this study session");
    }

    public void assertSameOwner(Long ownerId, String message) {
        User user = getAuthenticatedUser();
        if (!ownerId.equals(user.getId())) {
            throw new AccessDeniedException(message);
        }
    }
}


