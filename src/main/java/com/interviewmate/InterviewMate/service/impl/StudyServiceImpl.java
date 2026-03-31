package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.dto.GenerateStudyQuestionsRequest;
import com.interviewmate.InterviewMate.dto.StartStudyRequest;
import com.interviewmate.InterviewMate.dto.StudySessionResponse;
import com.interviewmate.InterviewMate.entity.StudyQuestion;
import com.interviewmate.InterviewMate.entity.StudySession;
import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.enums.StudyDifficulty;
import com.interviewmate.InterviewMate.enums.StudyQuestionType;
import com.interviewmate.InterviewMate.exception.BadRequestException;
import com.interviewmate.InterviewMate.exception.EntityNotFoundException;
import com.interviewmate.InterviewMate.mapper.StudyMapper;
import com.interviewmate.InterviewMate.repository.StudyQuestionRepository;
import com.interviewmate.InterviewMate.repository.StudySessionRepository;
import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.service.StudyService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StudyServiceImpl implements StudyService {

    private final StudySessionRepository studySessionRepository;
    private final StudyQuestionRepository studyQuestionRepository;
    private final StudyMapper studyMapper;
    private final UserRepository userRepository;

    public StudyServiceImpl(StudySessionRepository studySessionRepository,
                            StudyQuestionRepository studyQuestionRepository,
                            StudyMapper studyMapper,
                            UserRepository userRepository) {
        this.studySessionRepository = studySessionRepository;
        this.studyQuestionRepository = studyQuestionRepository;
        this.studyMapper = studyMapper;
        this.userRepository = userRepository;
    }

    @Override
    public StudySessionResponse start(StartStudyRequest request) {
        String topic = resolveTopic(request);

        StudySession session = new StudySession();
        session.setUser(getAuthenticatedUser());
        session.setTopic(topic);

        StudySession savedSession = studySessionRepository.save(session);
        return studyMapper.toSessionResponse(savedSession, List.of());
    }

    @Override
    @Transactional
    public StudySessionResponse generateQuestions(GenerateStudyQuestionsRequest request) {
        StudySession session = findStudySessionOrThrow(request.getStudySessionId());
        verifyOwnership(session);

        // Regenerate questions from scratch for deterministic study flow.
        studyQuestionRepository.deleteByStudySessionId(session.getId());

        List<StudyQuestion> questions = buildStubQuestions(session);
        studyQuestionRepository.saveAll(questions);

        List<StudyQuestion> persistedQuestions = studyQuestionRepository.findByStudySessionIdOrderByOrderIndex(session.getId());
        return studyMapper.toSessionResponse(session, persistedQuestions);
    }

    @Override
    public StudySessionResponse getById(UUID studySessionId) {
        StudySession session = findStudySessionOrThrow(studySessionId);
        verifyOwnership(session);

        List<StudyQuestion> questions = studyQuestionRepository.findByStudySessionIdOrderByOrderIndex(studySessionId);
        return studyMapper.toSessionResponse(session, questions);
    }

    private StudySession findStudySessionOrThrow(UUID studySessionId) {
        return studySessionRepository.findById(studySessionId)
                .orElseThrow(() -> new EntityNotFoundException("Study session not found: " + studySessionId));
    }

    private String resolveTopic(StartStudyRequest request) {
        String rawTopic = request.getTopic() == null ? "" : request.getTopic().trim();
        String rawAudio = request.getAudioFile() == null ? "" : request.getAudioFile().trim();

        if (rawTopic.isEmpty() && rawAudio.isEmpty()) {
            throw new BadRequestException("At least one value is required: topic or audioFile");
        }

        if (!rawTopic.isEmpty()) {
            return rawTopic;
        }

        return "Topic inferred from audio note";
    }

    private List<StudyQuestion> buildStubQuestions(StudySession session) {
        String topic = session.getTopic();
        List<StudyQuestion> questions = new ArrayList<>();

        questions.add(buildQuestion(session, 1, "Explain the fundamentals of " + topic, StudyDifficulty.BASIC, StudyQuestionType.THEORETICAL));
        questions.add(buildQuestion(session, 2, "Describe a practical use case of " + topic, StudyDifficulty.BASIC, StudyQuestionType.PRACTICAL));
        questions.add(buildQuestion(session, 3, "What are common pitfalls when working with " + topic + "?", StudyDifficulty.INTERMEDIATE, StudyQuestionType.THEORETICAL));
        questions.add(buildQuestion(session, 4, "Design a small solution using " + topic, StudyDifficulty.INTERMEDIATE, StudyQuestionType.PRACTICAL));
        questions.add(buildQuestion(session, 5, "Compare trade-offs for advanced decisions in " + topic, StudyDifficulty.ADVANCED, StudyQuestionType.THEORETICAL));
        questions.add(buildQuestion(session, 6, "Propose a production-ready strategy for " + topic, StudyDifficulty.ADVANCED, StudyQuestionType.PRACTICAL));

        return questions;
    }

    private StudyQuestion buildQuestion(StudySession session,
                                        int orderIndex,
                                        String questionText,
                                        StudyDifficulty difficulty,
                                        StudyQuestionType type) {
        StudyQuestion question = new StudyQuestion();
        question.setStudySession(session);
        question.setOrderIndex(orderIndex);
        question.setQuestionText(questionText);
        question.setDifficulty(difficulty);
        question.setType(type);
        return question;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
    }

    private void verifyOwnership(StudySession session) {
        User user = getAuthenticatedUser();
        if (!session.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the owner of this study session");
        }
    }
}

