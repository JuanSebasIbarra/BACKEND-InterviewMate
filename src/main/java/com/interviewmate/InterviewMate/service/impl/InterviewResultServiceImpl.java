package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.dto.CreateResultRequest;
import com.interviewmate.InterviewMate.dto.InterviewResultResponse;
import com.interviewmate.InterviewMate.entity.InterviewResult;
import com.interviewmate.InterviewMate.entity.InterviewSession;
import com.interviewmate.InterviewMate.exception.EntityNotFoundException;
import com.interviewmate.InterviewMate.mapper.InterviewResultMapper;
import com.interviewmate.InterviewMate.repository.InterviewResultRepository;
import com.interviewmate.InterviewMate.service.AccessControlService;
import com.interviewmate.InterviewMate.service.InterviewResultService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InterviewResultServiceImpl implements InterviewResultService {

    private final InterviewResultRepository resultRepository;
    private final InterviewResultMapper resultMapper;
    private final AccessControlService accessControlService;

    public InterviewResultServiceImpl(InterviewResultRepository resultRepository,
                                      InterviewResultMapper resultMapper,
                                      AccessControlService accessControlService) {
        this.resultRepository = resultRepository;
        this.resultMapper = resultMapper;
        this.accessControlService = accessControlService;
    }

    @Override
    public InterviewResultResponse getBySession(UUID sessionId) {
        InterviewResult result = resultRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Result not found for session: " + sessionId));
        accessControlService.assertOwnership(result);
        return resultMapper.toResponse(result);
    }

    @Override
    public List<InterviewResultResponse> getByAuthenticatedUser() {
        Long userId = accessControlService.getAuthenticatedUser().getId();
        return resultRepository.findBySessionTemplateUserId(userId).stream()
                .map(resultMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<InterviewResultResponse> getByAuthenticatedUserPaged(Pageable pageable) {
        Long userId = accessControlService.getAuthenticatedUser().getId();
        return resultRepository.findBySessionTemplateUserId(userId, pageable)
                .map(resultMapper::toResponse);
    }

    @Override
    public InterviewResultResponse save(CreateResultRequest request, InterviewSession session) {
        InterviewResult result = resultRepository.findBySessionId(session.getId())
                .orElseGet(() -> resultMapper.toEntity(request, session));
        result.setSession(session);
        result.setGeneralFeedback(request.getGeneralFeedback());
        result.setStrengths(request.getStrengths());
        result.setWeaknesses(request.getWeaknesses());
        result.setTotalScore(request.getTotalScore());
        result.setAiModel(request.getAiModel());
        result.setTotalTokensUsed(request.getTotalTokensUsed());
        return resultMapper.toResponse(resultRepository.save(result));
    }

}
