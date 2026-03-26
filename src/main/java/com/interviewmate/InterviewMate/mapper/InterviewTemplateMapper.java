package com.interviewmate.InterviewMate.mapper;

import com.interviewmate.InterviewMate.dto.CreateInterviewTemplateRequest;
import com.interviewmate.InterviewMate.dto.InterviewTemplateResponse;
import com.interviewmate.InterviewMate.entity.InterviewTemplate;
import com.interviewmate.InterviewMate.entity.User;
import org.springframework.stereotype.Component;

@Component
public class InterviewTemplateMapper {

    public InterviewTemplate toEntity(CreateInterviewTemplateRequest req, User user) {
        InterviewTemplate template = new InterviewTemplate();
        template.setUser(user);
        template.setEnterprise(req.getEnterprise());
        template.setType(req.getType());
        template.setPosition(req.getPosition());
        template.setWorkingArea(req.getWorkingArea());
        template.setDescription(req.getDescription());
        template.setRequirements(req.getRequirements());
        template.setGoals(req.getGoals());
        template.setBusinessContext(req.getBusinessContext());
        return template;
    }

    public InterviewTemplateResponse toResponse(InterviewTemplate template) {
        InterviewTemplateResponse response = new InterviewTemplateResponse();
        response.setId(template.getId());
        response.setUserId(template.getUser().getId());
        response.setUserFullName(template.getUser().getUsername());
        response.setEnterprise(template.getEnterprise());
        response.setType(template.getType());
        response.setPosition(template.getPosition());
        response.setWorkingArea(template.getWorkingArea());
        response.setDescription(template.getDescription());
        response.setRequirements(template.getRequirements());
        response.setGoals(template.getGoals());
        response.setBusinessContext(template.getBusinessContext());
        response.setStatus(template.getStatus());
        response.setCreatedAt(template.getCreatedAt());
        response.setUpdatedAt(template.getUpdatedAt());
        return response;
    }
}
