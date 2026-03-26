package com.interviewmate.InterviewMate.mapper;

import com.interviewmate.InterviewMate.dto.CreateResultRequest;
import com.interviewmate.InterviewMate.dto.InterviewResultResponse;
import com.interviewmate.InterviewMate.entity.InterviewResult;
import com.interviewmate.InterviewMate.entity.InterviewSession;
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
        InterviewResultResponse response = new InterviewResultResponse();
        response.setId(result.getId());
        response.setSessionId(result.getSession().getId());
        response.setAttemptNumber(result.getSession().getAttemptNumber());
        response.setGeneralFeedback(result.getGeneralFeedback());
        response.setStrengths(result.getStrengths());
        response.setWeaknesses(result.getWeaknesses());
        response.setTotalScore(result.getTotalScore());
        response.setStatus(result.getStatus());
        response.setAiModel(result.getAiModel());
        response.setTotalTokensUsed(result.getTotalTokensUsed());
        response.setGeneratedAt(result.getGeneratedAt());
        return response;
    }
}
