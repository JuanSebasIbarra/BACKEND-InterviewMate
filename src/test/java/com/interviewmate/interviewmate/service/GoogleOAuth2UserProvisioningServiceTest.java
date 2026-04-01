package com.interviewmate.interviewmate.service;

import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.enums.AuthProvider;
import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.security.oauth2.GoogleOAuth2UserProvisioningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GoogleOAuth2UserProvisioningServiceTest {

    private UserRepository userRepository;
    private GoogleOAuth2UserProvisioningService provisioningService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        provisioningService = new GoogleOAuth2UserProvisioningService(userRepository);
    }

    @Test
    void upsertGoogleUser_whenNewEmail_createsUser() {
        Map<String, Object> attrs = Map.of(
                "email", "john@example.com",
                "sub", "google-123",
                "email_verified", true,
                "given_name", "John",
                "picture", "https://img.example.com/john.jpg"
        );

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = provisioningService.upsertGoogleUser(attrs);

        assertEquals("john@example.com", user.getEmail());
        assertEquals("google-123", user.getGoogleId());
        assertEquals(AuthProvider.GOOGLE, user.getAuthProvider());
        assertEquals(Set.of("ROLE_USER"), user.getRoles());
        assertNotNull(user.getPassword());
    }

    @Test
    void upsertGoogleUser_whenEmailNotVerified_throws() {
        Map<String, Object> attrs = Map.of(
                "email", "john@example.com",
                "sub", "google-123",
                "email_verified", false
        );

        assertThrows(RuntimeException.class, () -> provisioningService.upsertGoogleUser(attrs));
    }
}


