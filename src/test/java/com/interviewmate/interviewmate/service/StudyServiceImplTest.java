package com.interviewmate.interviewmate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.InterviewMate.config.AiServiceProperties;
import com.interviewmate.InterviewMate.dto.StartStudyRequest;
import com.interviewmate.InterviewMate.entity.InterviewTemplate;
import com.interviewmate.InterviewMate.entity.StudyQuestion;
import com.interviewmate.InterviewMate.entity.StudySession;
import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.enums.InterviewType;
import com.interviewmate.InterviewMate.enums.StudyDifficulty;
import com.interviewmate.InterviewMate.enums.StudyQuestionType;
import com.interviewmate.InterviewMate.exception.BadRequestException;
import com.interviewmate.InterviewMate.mapper.StudyMapper;
import com.interviewmate.InterviewMate.repository.InterviewTemplateRepository;
import com.interviewmate.InterviewMate.repository.StudyQuestionRepository;
import com.interviewmate.InterviewMate.repository.StudySessionRepository;
import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.service.impl.StudyServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class StudyServiceImplTest {

    private StudySessionRepository studySessionRepository;
    private StudyQuestionRepository studyQuestionRepository;
    private InterviewTemplateRepository templateRepository;
    private UserRepository userRepository;
    private StudyServiceImpl studyService;

    @BeforeEach
    void setUp() {
        studySessionRepository = Mockito.mock(StudySessionRepository.class);
        studyQuestionRepository = Mockito.mock(StudyQuestionRepository.class);
        templateRepository = Mockito.mock(InterviewTemplateRepository.class);
        userRepository = Mockito.mock(UserRepository.class);

        studyService = new StudyServiceImpl(
                studySessionRepository,
                studyQuestionRepository,
                templateRepository,
                new StudyMapper(),
                userRepository,
                new AiServiceProperties(),
                new ObjectMapper()
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", "n/a", List.of())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void start_whenTemplateExists_createsSessionAndQuestions() {
        User user = buildUser(1L, "alice");
        InterviewTemplate template = buildTemplate(user);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(templateRepository.findById(template.getId())).thenReturn(Optional.of(template));
        when(studySessionRepository.save(any(StudySession.class))).thenAnswer(invocation -> {
            StudySession saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            saved.setCreatedAt(LocalDateTime.now());
            return saved;
        });
        when(studyQuestionRepository.findByStudySessionIdOrderByOrderIndex(any(UUID.class)))
                .thenAnswer(invocation -> buildPersistedQuestionsWithOrder(invocation.getArgument(0)));

        var response = studyService.start(StartStudyRequest.builder()
                .templateId(template.getId())
                .topic("Spring Security")
                .build());

        assertNotNull(response.getId());
        assertEquals(template.getId(), response.getTemplateId());
        assertEquals("Spring Security", response.getTopic());
        assertNotNull(response.getQuestions());
        assertEquals(6, response.getQuestions().size());
    }

    @Test
    void start_whenTemplateMissing_throwsBadRequest() {
        User user = buildUser(1L, "alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class,
                () -> studyService.start(StartStudyRequest.builder().build()));
    }

    @Test
    void start_whenTemplateNotFound_throwsEntityNotFound() {
        User user = buildUser(1L, "alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        assertThrows(com.interviewmate.InterviewMate.exception.EntityNotFoundException.class,
                () -> studyService.start(StartStudyRequest.builder().templateId(UUID.randomUUID()).build()));
    }

    @Test
    void start_withoutTopic_usesTemplateContextForFallbackQuestions() {
        UUID sessionId = UUID.randomUUID();

        User user = buildUser(1L, "alice");
        InterviewTemplate template = buildTemplate(user);
        StudySession session = new StudySession();
        session.setId(sessionId);
        session.setUser(user);
        session.setTemplate(template);
        session.setTopic("Backend Engineer - Platform");
        session.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(templateRepository.findById(template.getId())).thenReturn(Optional.of(template));
        when(studySessionRepository.save(any(StudySession.class))).thenReturn(session);
        when(studyQuestionRepository.findByStudySessionIdOrderByOrderIndex(eq(sessionId)))
                .thenAnswer(invocation -> buildPersistedQuestions(session));

        var response = studyService.start(StartStudyRequest.builder().templateId(template.getId()).build());

        assertEquals(6, response.getQuestions().size());
        assertEquals(StudyDifficulty.BASIC, response.getQuestions().get(0).getDifficulty());
        assertEquals(StudyQuestionType.PRACTICAL, response.getQuestions().get(1).getType());
        assertEquals(StudyDifficulty.ADVANCED, response.getQuestions().get(5).getDifficulty());
    }

    private InterviewTemplate buildTemplate(User user) {
        InterviewTemplate template = new InterviewTemplate();
        template.setId(UUID.randomUUID());
        template.setUser(user);
        template.setEnterprise("Acme");
        template.setPosition("Backend Engineer");
        template.setType(InterviewType.TECHNICAL);
        template.setWorkingArea("Platform");
        template.setRequirements("Java, Spring, SQL");
        return template;
    }

    private User buildUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@mail.com");
        user.setPassword("secret");
        user.setRoles(Set.of("ROLE_USER"));
        return user;
    }

    private List<StudyQuestion> buildPersistedQuestions(StudySession session) {
        StudyQuestion q1 = buildQuestion(session, 1, "Q1", StudyDifficulty.BASIC, StudyQuestionType.THEORETICAL);
        StudyQuestion q2 = buildQuestion(session, 2, "Q2", StudyDifficulty.BASIC, StudyQuestionType.PRACTICAL);
        StudyQuestion q3 = buildQuestion(session, 3, "Q3", StudyDifficulty.INTERMEDIATE, StudyQuestionType.THEORETICAL);
        StudyQuestion q4 = buildQuestion(session, 4, "Q4", StudyDifficulty.INTERMEDIATE, StudyQuestionType.PRACTICAL);
        StudyQuestion q5 = buildQuestion(session, 5, "Q5", StudyDifficulty.ADVANCED, StudyQuestionType.THEORETICAL);
        StudyQuestion q6 = buildQuestion(session, 6, "Q6", StudyDifficulty.ADVANCED, StudyQuestionType.PRACTICAL);
        return List.of(q1, q2, q3, q4, q5, q6);
    }

    private List<StudyQuestion> buildPersistedQuestionsWithOrder(UUID sessionId) {
        StudySession session = new StudySession();
        session.setId(sessionId);
        session.setTopic("Spring Security");
        return buildPersistedQuestions(session);
    }

    private StudyQuestion buildQuestion(StudySession session,
                                        int index,
                                        String text,
                                        StudyDifficulty difficulty,
                                        StudyQuestionType type) {
        StudyQuestion question = new StudyQuestion();
        question.setId(UUID.randomUUID());
        question.setStudySession(session);
        question.setOrderIndex(index);
        question.setQuestionText(text);
        question.setDifficulty(difficulty);
        question.setType(type);
        return question;
    }
}

