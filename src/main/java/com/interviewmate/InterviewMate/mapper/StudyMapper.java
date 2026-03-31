package com.interviewmate.InterviewMate.mapper;

import com.interviewmate.InterviewMate.dto.StudyQuestionResponse;
import com.interviewmate.InterviewMate.dto.StudySessionResponse;
import com.interviewmate.InterviewMate.entity.StudyQuestion;
import com.interviewmate.InterviewMate.entity.StudySession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudyMapper {

    public StudyQuestionResponse toQuestionResponse(StudyQuestion question) {
        StudyQuestionResponse response = new StudyQuestionResponse();
        response.setId(question.getId());
        response.setOrderIndex(question.getOrderIndex());
        response.setQuestionText(question.getQuestionText());
        response.setDifficulty(question.getDifficulty());
        response.setType(question.getType());
        return response;
    }

    public StudySessionResponse toSessionResponse(StudySession session, List<StudyQuestion> questions) {
        StudySessionResponse response = new StudySessionResponse();
        response.setId(session.getId());
        response.setTopic(session.getTopic());
        response.setCreatedAt(session.getCreatedAt());
        response.setQuestions(questions.stream().map(this::toQuestionResponse).toList());
        return response;
    }
}

