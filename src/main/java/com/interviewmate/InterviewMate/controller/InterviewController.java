package com.interviewmate.InterviewMate.controller;

import com.interviewmate.InterviewMate.dto.AnswerResponse;
import com.interviewmate.InterviewMate.dto.InterviewResponse;
import com.interviewmate.InterviewMate.dto.StartInterviewRequest;
import com.interviewmate.InterviewMate.dto.SubmitAnswerRequest;
import com.interviewmate.InterviewMate.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/entrevistas")
@RequiredArgsConstructor
@Slf4j
public class InterviewController {

    private final InterviewService interviewService;

    /**
     * POST /entrevistas/start
     * Inicia una nueva sesión de entrevista y genera preguntas con IA
     */
    @PostMapping("/start")
    public ResponseEntity<InterviewResponse> startInterview(@Valid @RequestBody StartInterviewRequest request) {
        String username = getCurrentUsername();
        log.info("Solicitud de inicio de entrevista para usuario: {}", username);

        InterviewResponse response = interviewService.startInterview(request, username);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /respuestas
     * Registra la respuesta del usuario a una pregunta específica
     */
    @PostMapping("/respuestas")
    public ResponseEntity<AnswerResponse> submitAnswer(@Valid @RequestBody SubmitAnswerRequest request) {
        String username = getCurrentUsername();
        log.info("Solicitud de registro de respuesta para usuario: {}", username);

        AnswerResponse response = interviewService.submitAnswer(request, username);
        return ResponseEntity.ok(response);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
