package com.interviewmate.InterviewMate.service.impl;

import com.interviewmate.InterviewMate.dto.AnswerResponse;
import com.interviewmate.InterviewMate.dto.InterviewResponse;
import com.interviewmate.InterviewMate.dto.QuestionResponse;
import com.interviewmate.InterviewMate.dto.StartInterviewRequest;
import com.interviewmate.InterviewMate.dto.SubmitAnswerRequest;
import com.interviewmate.InterviewMate.entity.Answer;
import com.interviewmate.InterviewMate.entity.Interview;
import com.interviewmate.InterviewMate.entity.Question;
import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.exception.ResourceNotFoundException;
import com.interviewmate.InterviewMate.repository.AnswerRepository;
import com.interviewmate.InterviewMate.repository.InterviewRepository;
import com.interviewmate.InterviewMate.repository.QuestionRepository;
import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.service.AIService;
import com.interviewmate.InterviewMate.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final AIService aiService;

    @Override
    public InterviewResponse startInterview(StartInterviewRequest request, String username) {
        log.info("Iniciando entrevista para usuario: {} - Tipo: {}, Dificultad: {}",
                username, request.getTipoEntrevista(), request.getNivelDificultad());

        // 1. Obtener usuario
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        // 2. Verificar que el usuario tenga perfil profesional
        if (user.getPerfilProfesional() == null || user.getPerfilProfesional().trim().isEmpty()) {
            throw new IllegalStateException("Debe completar su perfil profesional antes de iniciar una entrevista");
        }

        // 3. Generar preguntas usando IA
        List<String> questionTexts = aiService.generateQuestions(
                user.getPerfilProfesional(),
                request.getTipoEntrevista(),
                request.getNivelDificultad()
        );

        // 4. Crear la entrevista
        Interview interview = Interview.builder()
                .user(user)
                .title("Entrevista " + request.getTipoEntrevista() + " - " + request.getNivelDificultad())
                .description("Entrevista generada automáticamente")
                .status(Interview.InterviewStatus.IN_PROGRESS)
                .build();

        Interview savedInterview = interviewRepository.save(interview);

        // 5. Crear las preguntas
        List<Question> questions = questionTexts.stream()
                .map((text) -> Question.builder()
                        .interview(savedInterview)
                        .text(text)
                        .type(Question.QuestionType.TECHNICAL)
                        .questionOrder(questionTexts.indexOf(text) + 1)
                        .build())
                .collect(Collectors.toList());

        List<Question> savedQuestions = questionRepository.saveAll(questions);

        log.info("Entrevista creada exitosamente con ID: {} y {} preguntas", savedInterview.getId(), savedQuestions.size());

        // 6. Retornar respuesta
        return mapToInterviewResponse(savedInterview, savedQuestions);
    }

    @Override
    public AnswerResponse submitAnswer(SubmitAnswerRequest request, String username) {
        log.info("Registrando respuesta para pregunta ID: {} por usuario: {}", request.getQuestionId(), username);

        // 1. Verificar que la pregunta existe y pertenece al usuario
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada: " + request.getQuestionId()));

        if (!question.getInterview().getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("La pregunta no pertenece al usuario actual");
        }

        // 2. Verificar que la entrevista esté en progreso
        if (question.getInterview().getStatus() != Interview.InterviewStatus.IN_PROGRESS) {
            throw new IllegalStateException("La entrevista no está en progreso");
        }

        // 3. Verificar que no exista ya una respuesta para esta pregunta
        boolean hasAnswer = answerRepository.existsByQuestion(question);
        if (hasAnswer) {
            throw new IllegalStateException("Ya existe una respuesta para esta pregunta");
        }

        // 4. Crear la respuesta
        Answer answer = Answer.builder()
                .question(question)
                .text(request.getText())
                .build();

        Answer savedAnswer = answerRepository.save(answer);

        log.info("Respuesta registrada exitosamente con ID: {}", savedAnswer.getId());

        // 5. Retornar respuesta
        return mapToAnswerResponse(savedAnswer);
    }

    private InterviewResponse mapToInterviewResponse(Interview interview, List<Question> questions) {
        List<QuestionResponse> questionResponses = questions.stream()
                .map(this::mapToQuestionResponse)
                .collect(Collectors.toList());

        return InterviewResponse.builder()
                .id(interview.getId())
                .title(interview.getTitle())
                .description(interview.getDescription())
                .status(interview.getStatus().name())
                .questions(questionResponses)
                .createdAt(interview.getCreatedAt())
                .updatedAt(interview.getUpdatedAt())
                .build();
    }

    private QuestionResponse mapToQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .text(question.getText())
                .type(question.getType().name())
                .questionOrder(question.getQuestionOrder())
                .createdAt(question.getCreatedAt())
                .build();
    }

    private AnswerResponse mapToAnswerResponse(Answer answer) {
        return AnswerResponse.builder()
                .id(answer.getId())
                .questionId(answer.getQuestion().getId())
                .text(answer.getText())
                .score(answer.getScore())
                .createdAt(answer.getCreatedAt())
                .build();
    }
}
