package com.interviewmate.InterviewMate.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartStudyRequest {

    private String audioFile;
    private String topic;
}

