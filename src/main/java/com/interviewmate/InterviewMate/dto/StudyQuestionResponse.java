package com.interviewmate.InterviewMate.dto;

import com.interviewmate.InterviewMate.enums.StudyDifficulty;
import com.interviewmate.InterviewMate.enums.StudyQuestionType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyQuestionResponse {

    private UUID id;
    private int orderIndex;
    private String questionText;
    private StudyDifficulty difficulty;
    private StudyQuestionType type;
}

