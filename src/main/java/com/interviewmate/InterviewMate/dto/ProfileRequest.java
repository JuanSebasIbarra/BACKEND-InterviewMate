package com.interviewmate.InterviewMate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {

    @Size(max = 50, message = "Username must not exceed 50 characters")
    private String username;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(max = 5000, message = "Perfil profesional must not exceed 5000 characters")
    private String perfilProfesional;

    @Size(max = 1000, message = "Profile picture URL must not exceed 1000 characters")
    private String profilePictureUrl;
}
