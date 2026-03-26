package com.interviewmate.InterviewMate.mapper;

import com.interviewmate.InterviewMate.dto.QuestionResponse;
import com.interviewmate.InterviewMate.entity.InterviewQuestion;
import com.interviewmate.InterviewMate.entity.InterviewSession;
import org.springframework.stereotype.Component;

@Component
public class InterviewQuestionMapper {

    public InterviewQuestion toEntity(InterviewSession session, String question, int orderIndex, String aiModel) {
        InterviewQuestion entity = new InterviewQuestion();
        entity.setSession(session);
        entity.setQuestion(question);
        entity.setOrderIndex(orderIndex);
        entity.setAiModel(aiModel);
        return entity;
    }

    public QuestionResponse toResponse(InterviewQuestion entity) {
        QuestionResponse response = new QuestionResponse();
        response.setId(entity.getId());
        response.setSessionId(entity.getSession().getId());
        response.setOrderIndex(entity.getOrderIndex());
        response.setQuestion(entity.getQuestion());
        response.setAnswer(entity.getAnswer());
        response.setAiFeedback(entity.getAiFeedback());
        response.setScore(entity.getScore());
        response.setAiModel(entity.getAiModel());
        response.setCreatedAt(entity.getCreatedAt());
        response.setAnsweredAt(entity.getAnsweredAt());
        return response;
    }
}
