package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.dto.QuestionResponse;
import com.interviewmate.InterviewMate.dto.SubmitAnswerRequest;
import com.interviewmate.InterviewMate.entity.InterviewQuestion;
import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.exception.EntityNotFoundException;
import com.interviewmate.InterviewMate.mapper.InterviewQuestionMapper;
import com.interviewmate.InterviewMate.repository.InterviewQuestionRepository;
import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.service.AiInterviewService;
import com.interviewmate.InterviewMate.service.InterviewQuestionService;
import com.interviewmate.InterviewMate.service.InterviewSessionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InterviewQuestionServiceImpl implements InterviewQuestionService {

    private final InterviewQuestionRepository questionRepository;
    private final InterviewQuestionMapper questionMapper;
    private final AiInterviewService aiInterviewService;
    private final InterviewSessionService sessionService;
    private final UserRepository userRepository;

    public InterviewQuestionServiceImpl(InterviewQuestionRepository questionRepository,
                                        InterviewQuestionMapper questionMapper,
                                        AiInterviewService aiInterviewService,
                                        InterviewSessionService sessionService,
                                        UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
        this.aiInterviewService = aiInterviewService;
        this.sessionService = sessionService;
        this.userRepository = userRepository;
    }

    @Override
    public List<QuestionResponse> getBySession(UUID sessionId) {
        return questionRepository.findBySessionIdOrderByOrderIndex(sessionId).stream()
                .peek(this::verifyOwnership)
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponse getById(UUID questionId) {
        InterviewQuestion question = findOrThrow(questionId);
        verifyOwnership(question);
        return questionMapper.toResponse(question);
    }

    @Override
    public QuestionResponse submitAnswer(UUID questionId, SubmitAnswerRequest request) {
        InterviewQuestion question = findOrThrow(questionId);
        verifyOwnership(question);
        question.setAnswer(request.getAnswer());
        question.setAnsweredAt(LocalDateTime.now());
        questionRepository.save(question);

        aiInterviewService.evaluateAnswer(questionId);

        UUID sessionId = question.getSession().getId();
        List<InterviewQuestion> allQuestions = questionRepository.findBySessionId(sessionId);
        boolean allAnswered = allQuestions.stream().allMatch(q -> q.getAnswer() != null && !q.getAnswer().isBlank());

        if (allAnswered) {
            aiInterviewService.generateResult(sessionId);
            sessionService.complete(sessionId);
        }

        return questionMapper.toResponse(findOrThrow(questionId));
    }

    private InterviewQuestion findOrThrow(UUID questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found: " + questionId));
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
    }

    private void verifyOwnership(InterviewQuestion question) {
        User user = getAuthenticatedUser();
        if (!question.getSession().getTemplate().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the owner of this interview question");
        }
    }
}
