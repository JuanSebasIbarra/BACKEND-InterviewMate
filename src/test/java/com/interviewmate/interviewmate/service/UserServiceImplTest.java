package com.interviewmate.interviewmate.service;

import com.interviewmate.InterviewMate.dto.UserRequest;
import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void createUser_whenNewUser_succeeds() {
        UserRequest req = UserRequest.builder().username("juan").email("j@example.com").password("Secret123").build();
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(req.getUsername())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        var res = userService.createUser(req);
        assertNotNull(res);
        assertEquals("juan", res.getUsername());
        assertEquals("j@example.com", res.getEmail());
        assertNotNull(res.getId());
    }

    @Test
    void createUser_whenEmailExists_throws() {
        UserRequest req = UserRequest.builder().username("juan").email("j@example.com").password("Secret123").build();
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);
        assertThrows(RuntimeException.class, () -> userService.createUser(req));
    }
}
