package com.interviewmate.InterviewMate.mapper;

import com.interviewmate.InterviewMate.dto.CreateResultRequest;
import com.interviewmate.InterviewMate.dto.InterviewResultResponse;
import com.interviewmate.InterviewMate.entity.InterviewResult;
import com.interviewmate.InterviewMate.entity.InterviewSession;
import com.interviewmate.InterviewMate.entity.InterviewTemplate;
import org.springframework.stereotype.Component;

@Component
public class InterviewResultMapper {

    public InterviewResult toEntity(CreateResultRequest req, InterviewSession session) {
        InterviewResult result = new InterviewResult();
        result.setSession(session);
        result.setGeneralFeedback(req.getGeneralFeedback());
        result.setStrengths(req.getStrengths());
        result.setWeaknesses(req.getWeaknesses());
        result.setTotalScore(req.getTotalScore());
        result.setAiModel(req.getAiModel());
        result.setTotalTokensUsed(req.getTotalTokensUsed());
        return result;
    }

    public InterviewResultResponse toResponse(InterviewResult result) {
        InterviewSession session = result.getSession();
        InterviewTemplate template = session.getTemplate();

        return InterviewResultResponse.builder()
                .id(result.getId())
                .sessionId(session.getId())
                .attemptNumber(session.getAttemptNumber())
                .templateId(template.getId())
                .enterprise(template.getEnterprise())
                .position(template.getPosition())
                .interviewType(template.getType())
                .generalFeedback(result.getGeneralFeedback())
                .strengths(result.getStrengths())
                .weaknesses(result.getWeaknesses())
                .totalScore(result.getTotalScore())
                .status(result.getStatus())
                .aiModel(result.getAiModel())
                .totalTokensUsed(result.getTotalTokensUsed())
                .generatedAt(result.getGeneratedAt())
                .sessionStartedAt(session.getStartedAt())
                .build();
    }
}


