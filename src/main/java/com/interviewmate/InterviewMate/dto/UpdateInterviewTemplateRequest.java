package com.interviewmate.InterviewMate.dto;

import com.interviewmate.InterviewMate.enums.InterviewType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInterviewTemplateRequest {

    private String enterprise;
    private InterviewType type;
    private String position;
    private String workingArea;
    private String description;
    private String requirements;
    private String goals;
    private String businessContext;
}
