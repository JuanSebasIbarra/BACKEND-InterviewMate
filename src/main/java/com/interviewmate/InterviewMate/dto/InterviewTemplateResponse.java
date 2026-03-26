package com.interviewmate.InterviewMate.dto;

import com.interviewmate.InterviewMate.enums.InterviewStatus;
import com.interviewmate.InterviewMate.enums.InterviewType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewTemplateResponse {

    private UUID id;
    private Long userId;
    private String userFullName;
    private String enterprise;
    private InterviewType type;
    private String position;
    private String workingArea;
    private String description;
    private String requirements;
    private String goals;
    private String businessContext;
    private InterviewStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
