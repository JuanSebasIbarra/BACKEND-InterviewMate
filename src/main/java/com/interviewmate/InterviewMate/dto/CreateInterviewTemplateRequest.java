package com.interviewmate.InterviewMate.dto;

import com.interviewmate.InterviewMate.enums.InterviewType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInterviewTemplateRequest {

    @NotBlank(message = "Enterprise is required")
    private String enterprise;

    @NotNull(message = "Interview type is required")
    private InterviewType type;

    @NotBlank(message = "Position is required")
    private String position;

    private String workingArea;
    private String description;
    private String requirements;
    private String goals;
    private String businessContext;
}
