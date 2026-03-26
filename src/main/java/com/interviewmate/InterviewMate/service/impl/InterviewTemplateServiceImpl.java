package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.dto.CreateInterviewTemplateRequest;
import com.interviewmate.InterviewMate.dto.InterviewTemplateResponse;
import com.interviewmate.InterviewMate.dto.UpdateInterviewTemplateRequest;
import com.interviewmate.InterviewMate.entity.InterviewTemplate;
import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.enums.InterviewStatus;
import com.interviewmate.InterviewMate.exception.EntityNotFoundException;
import com.interviewmate.InterviewMate.mapper.InterviewTemplateMapper;
import com.interviewmate.InterviewMate.repository.InterviewTemplateRepository;
import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.service.InterviewTemplateService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InterviewTemplateServiceImpl implements InterviewTemplateService {

    private final InterviewTemplateRepository templateRepository;
    private final InterviewTemplateMapper templateMapper;
    private final UserRepository userRepository;

    public InterviewTemplateServiceImpl(InterviewTemplateRepository templateRepository,
                                        InterviewTemplateMapper templateMapper,
                                        UserRepository userRepository) {
        this.templateRepository = templateRepository;
        this.templateMapper = templateMapper;
        this.userRepository = userRepository;
    }

    @Override
    public InterviewTemplateResponse create(CreateInterviewTemplateRequest request) {
        User user = getAuthenticatedUser();
        InterviewTemplate template = templateMapper.toEntity(request, user);
        return templateMapper.toResponse(templateRepository.save(template));
    }

    @Override
    public InterviewTemplateResponse update(UUID templateId, UpdateInterviewTemplateRequest request) {
        User user = getAuthenticatedUser();
        InterviewTemplate template = findOrThrow(templateId);
        verifyOwnership(template, user);

        if (request.getEnterprise() != null) template.setEnterprise(request.getEnterprise());
        if (request.getType() != null) template.setType(request.getType());
        if (request.getPosition() != null) template.setPosition(request.getPosition());
        if (request.getWorkingArea() != null) template.setWorkingArea(request.getWorkingArea());
        if (request.getDescription() != null) template.setDescription(request.getDescription());
        if (request.getRequirements() != null) template.setRequirements(request.getRequirements());
        if (request.getGoals() != null) template.setGoals(request.getGoals());
        if (request.getBusinessContext() != null) template.setBusinessContext(request.getBusinessContext());

        return templateMapper.toResponse(templateRepository.save(template));
    }

    @Override
    public InterviewTemplateResponse changeStatus(UUID templateId, InterviewStatus newStatus) {
        User user = getAuthenticatedUser();
        InterviewTemplate template = findOrThrow(templateId);
        verifyOwnership(template, user);
        template.setStatus(newStatus);
        return templateMapper.toResponse(templateRepository.save(template));
    }

    @Override
    public InterviewTemplateResponse getById(UUID templateId) {
        return templateMapper.toResponse(findOrThrow(templateId));
    }

    @Override
    public List<InterviewTemplateResponse> getAllByAuthenticatedUser() {
        User user = getAuthenticatedUser();
        return templateRepository.findByUserId(user.getId()).stream()
                .map(templateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID templateId) {
        User user = getAuthenticatedUser();
        InterviewTemplate template = findOrThrow(templateId);
        verifyOwnership(template, user);
        template.setStatus(InterviewStatus.ARCHIVED);
        templateRepository.save(template);
    }

    private InterviewTemplate findOrThrow(UUID templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template not found: " + templateId));
    }

    private void verifyOwnership(InterviewTemplate template, User user) {
        if (!template.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the owner of this template");
        }
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
    }
}
