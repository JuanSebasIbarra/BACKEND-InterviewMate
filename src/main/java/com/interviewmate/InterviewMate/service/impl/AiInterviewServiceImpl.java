package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.dto.CreateResultRequest;
import com.interviewmate.InterviewMate.entity.InterviewQuestion;
import com.interviewmate.InterviewMate.entity.InterviewSession;
import com.interviewmate.InterviewMate.exception.EntityNotFoundException;
import com.interviewmate.InterviewMate.mapper.InterviewQuestionMapper;
import com.interviewmate.InterviewMate.repository.InterviewQuestionRepository;
import com.interviewmate.InterviewMate.repository.InterviewSessionRepository;
import com.interviewmate.InterviewMate.service.AiInterviewService;
import com.interviewmate.InterviewMate.service.InterviewResultService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AiInterviewServiceImpl implements AiInterviewService {

    private final InterviewQuestionRepository questionRepository;
    private final InterviewSessionRepository sessionRepository;
    private final InterviewResultService resultService;
    private final InterviewQuestionMapper questionMapper;

    public AiInterviewServiceImpl(InterviewQuestionRepository questionRepository,
                                  InterviewSessionRepository sessionRepository,
                                  InterviewResultService resultService,
                                  InterviewQuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.sessionRepository = sessionRepository;
        this.resultService = resultService;
        this.questionMapper = questionMapper;
    }

    @Override
    public void generateQuestionsForSession(UUID sessionId) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + sessionId));
        String position = session.getTemplate().getPosition();

        List<InterviewQuestion> questions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            InterviewQuestion question = questionMapper.toEntity(
                    session,
                    "Question " + i + " for position: " + position,
                    i,
                    "stub"
            );
            questions.add(question);
        }
        questionRepository.saveAll(questions);
    }

    @Override
    public void evaluateAnswer(UUID questionId) {
        InterviewQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found: " + questionId));
        question.setAiFeedback("Stub feedback");
        question.setScore(75.0);
        question.setAiModel("stub");
        questionRepository.save(question);
    }

    @Override
    public void generateResult(UUID sessionId) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + sessionId));
        List<InterviewQuestion> questions = questionRepository.findBySessionId(sessionId);

        double avgScore = questions.stream()
                .filter(q -> q.getScore() != null)
                .mapToDouble(InterviewQuestion::getScore)
                .average()
                .orElse(0.0);

        CreateResultRequest request = new CreateResultRequest();
        request.setGeneralFeedback("Stub general feedback");
        request.setStrengths("Stub strengths");
        request.setWeaknesses("Stub weaknesses");
        request.setTotalScore(avgScore);
        request.setAiModel("stub");
        request.setTotalTokensUsed(0);

        resultService.save(request, session);
    }
}
