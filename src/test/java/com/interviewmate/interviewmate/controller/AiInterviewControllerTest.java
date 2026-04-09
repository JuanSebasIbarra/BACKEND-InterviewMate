package com.interviewmate.interviewmate.controller;

import com.interviewmate.InterviewMate.controller.AIInterviewController;
import com.interviewmate.InterviewMate.dto.ApiResponse;
import com.interviewmate.InterviewMate.dto.InterviewResultResponse;
import com.interviewmate.InterviewMate.enums.InterviewType;
import com.interviewmate.InterviewMate.enums.ResultStatus;
import com.interviewmate.InterviewMate.service.AiInterviewService;
import com.interviewmate.InterviewMate.service.InterviewResultService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiInterviewControllerTest {

    @Test
    void reviewSession_returnsGeneratedInterviewFeedback() {
        AiInterviewService aiInterviewService = Mockito.mock(AiInterviewService.class);
        InterviewResultService interviewResultService = Mockito.mock(InterviewResultService.class);
        AIInterviewController controller = new AIInterviewController(aiInterviewService, interviewResultService);

        UUID sessionId = UUID.randomUUID();
        InterviewResultResponse expected = InterviewResultResponse.builder()
                .sessionId(sessionId)
                .templateId(UUID.randomUUID())
                .position("Backend Engineer")
                .enterprise("Acme")
                .interviewType(InterviewType.TECHNICAL)
                .status(ResultStatus.PENDING_REVIEW)
                .generalFeedback("Buen desempeño general")
                .build();

        when(interviewResultService.getBySession(sessionId)).thenReturn(expected);

        ResponseEntity<ApiResponse<InterviewResultResponse>> response = controller.reviewSession(sessionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(expected, response.getBody().getData());
        verify(aiInterviewService).generateResult(sessionId);
    }
}


