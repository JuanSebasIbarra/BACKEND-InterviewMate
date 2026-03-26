package com.interviewmate.InterviewMate.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    //name
    //lastname - optional

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    // password_confirmation
//    @NotBlank
//    @Size(min = 8, max = 100)
//    private String password_comfirmation;
}


//login
// email o el username ---> DTO
// password