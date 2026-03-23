package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.dto.AnswerResponse;
import com.interviewmate.InterviewMate.dto.InterviewResponse;
import com.interviewmate.InterviewMate.dto.StartInterviewRequest;
import com.interviewmate.InterviewMate.dto.SubmitAnswerRequest;

public interface InterviewService {

    /**
     * Inicia una nueva sesión de entrevista
     * @param request Datos para iniciar la entrevista
     * @param username Usuario que inicia la entrevista
     * @return Respuesta con la entrevista creada y preguntas generadas
     */
    InterviewResponse startInterview(StartInterviewRequest request, String username);

    /**
     * Registra la respuesta del usuario a una pregunta
     * @param request Datos de la respuesta
     * @param username Usuario que responde
     * @return Respuesta con la respuesta registrada
     */
    AnswerResponse submitAnswer(SubmitAnswerRequest request, String username);
}
