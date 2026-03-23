package com.interviewmate.InterviewMate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.InterviewMate.dto.StartInterviewRequest;
import com.interviewmate.InterviewMate.dto.SubmitAnswerRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    void startInterview_shouldReturnInterviewWithQuestions() throws Exception {
        StartInterviewRequest request = StartInterviewRequest.builder()
                .tipoEntrevista("desarrollador backend")
                .nivelDificultad("junior")
                .build();

        mockMvc.perform(post("/entrevistas/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.questions").isArray())
                .andExpect(jsonPath("$.questions.length()").value(5));
    }

    @Test
    @WithMockUser(username = "testuser")
    void submitAnswer_shouldReturnAnswer() throws Exception {
        // First start an interview to get a question ID
        StartInterviewRequest startRequest = StartInterviewRequest.builder()
                .tipoEntrevista("desarrollador backend")
                .nivelDificultad("junior")
                .build();

        String startResponse = mockMvc.perform(post("/entrevistas/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract first question ID from response
        int questionIdStart = startResponse.indexOf("\"id\":");
        int questionIdEnd = startResponse.indexOf(",", questionIdStart);
        String questionIdStr = startResponse.substring(questionIdStart + 5, questionIdEnd);
        Long questionId = Long.parseLong(questionIdStr);

        // Now submit an answer
        SubmitAnswerRequest answerRequest = SubmitAnswerRequest.builder()
                .questionId(questionId)
                .text("Esta es mi respuesta a la pregunta")
                .build();

        mockMvc.perform(post("/entrevistas/respuestas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.questionId").value(questionId))
                .andExpect(jsonPath("$.text").value("Esta es mi respuesta a la pregunta"));
    }
}
