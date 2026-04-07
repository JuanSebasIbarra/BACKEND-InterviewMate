package com.interviewmate.InterviewMate.dto;

import com.interviewmate.InterviewMate.enums.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String perfilProfesional;
    private String profilePictureUrl;
    private AuthProvider authProvider;
    private Set<String> roles;
    private Instant createdAt;
}
