package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.dto.CreateResultRequest;
import com.interviewmate.InterviewMate.dto.InterviewResultResponse;
import com.interviewmate.InterviewMate.entity.InterviewResult;
import com.interviewmate.InterviewMate.entity.InterviewSession;
import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.exception.EntityNotFoundException;
import com.interviewmate.InterviewMate.mapper.InterviewResultMapper;
import com.interviewmate.InterviewMate.repository.InterviewResultRepository;
import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.service.InterviewResultService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InterviewResultServiceImpl implements InterviewResultService {

    private final InterviewResultRepository resultRepository;
    private final InterviewResultMapper resultMapper;
    private final UserRepository userRepository;

    public InterviewResultServiceImpl(InterviewResultRepository resultRepository,
                                      InterviewResultMapper resultMapper,
                                      UserRepository userRepository) {
        this.resultRepository = resultRepository;
        this.resultMapper = resultMapper;
        this.userRepository = userRepository;
    }

    @Override
    public InterviewResultResponse getBySession(UUID sessionId) {
        User user = getAuthenticatedUser();
        InterviewResult result = resultRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Result not found for session: " + sessionId));
        if (!result.getSession().getTemplate().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the owner of this interview result");
        }
        return resultMapper.toResponse(result);
    }

    @Override
    public List<InterviewResultResponse> getByAuthenticatedUser() {
        User user = getAuthenticatedUser();
        return resultRepository.findBySessionTemplateUserId(user.getId()).stream()
                .map(resultMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<InterviewResultResponse> getByAuthenticatedUserPaged(Pageable pageable) {
        User user = getAuthenticatedUser();
        return resultRepository.findBySessionTemplateUserId(user.getId(), pageable)
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

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
    }
}
