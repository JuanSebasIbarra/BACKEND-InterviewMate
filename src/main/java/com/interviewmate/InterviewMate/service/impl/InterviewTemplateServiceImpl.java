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
import com.interviewmate.InterviewMate.service.AccessControlService;
import com.interviewmate.InterviewMate.service.InterviewTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InterviewTemplateServiceImpl implements InterviewTemplateService {

    private final InterviewTemplateRepository templateRepository;
    private final InterviewTemplateMapper templateMapper;
    private final AccessControlService accessControlService;

    public InterviewTemplateServiceImpl(InterviewTemplateRepository templateRepository,
                                        InterviewTemplateMapper templateMapper,
                                        AccessControlService accessControlService) {
        this.templateRepository = templateRepository;
        this.templateMapper = templateMapper;
        this.accessControlService = accessControlService;
    }

    @Override
    public InterviewTemplateResponse create(CreateInterviewTemplateRequest request) {
        User user = accessControlService.getAuthenticatedUser();
        InterviewTemplate template = templateMapper.toEntity(request, user);
        return templateMapper.toResponse(templateRepository.save(template));
    }

    @Override
    public InterviewTemplateResponse update(UUID templateId, UpdateInterviewTemplateRequest request) {
        InterviewTemplate template = findOrThrow(templateId);
        accessControlService.assertOwnership(template);

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
        InterviewTemplate template = findOrThrow(templateId);
        accessControlService.assertOwnership(template);
        template.setStatus(newStatus);
        return templateMapper.toResponse(templateRepository.save(template));
    }

    @Override
    public InterviewTemplateResponse getById(UUID templateId) {
        return templateMapper.toResponse(findOrThrow(templateId));
    }

    @Override
    public List<InterviewTemplateResponse> getAllByAuthenticatedUser() {
        User user = accessControlService.getAuthenticatedUser();
        return templateRepository.findByUserId(user.getId()).stream()
                .map(templateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID templateId) {
        InterviewTemplate template = findOrThrow(templateId);
        accessControlService.assertOwnership(template);
        template.setStatus(InterviewStatus.ARCHIVED);
        templateRepository.save(template);
    }

    private InterviewTemplate findOrThrow(UUID templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template not found: " + templateId));
    }

}
