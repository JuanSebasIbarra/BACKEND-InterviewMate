package com.interviewmate.interviewmate.controller;

import com.interviewmate.InterviewMate.controller.AIInterviewController;
import com.interviewmate.InterviewMate.dto.EvaluationRequest;
import com.interviewmate.InterviewMate.dto.EvaluationResult;
import com.interviewmate.InterviewMate.service.AiInterviewService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class AiInterviewControllerTest {

    @Test
    void evaluate_returnsEvaluationResultFromService() {
        AiInterviewService aiInterviewService = Mockito.mock(AiInterviewService.class);
        AIInterviewController controller = new AIInterviewController(aiInterviewService);

        EvaluationRequest request = new EvaluationRequest(
                "How would you optimize a binary search implementation?",
                "I would validate input, discuss complexity and keep the algorithm iterative."
        );
        EvaluationResult expected = new EvaluationResult(
                88,
                List.of("Good algorithmic reasoning"),
                List.of("Missing edge-case examples"),
                "Strong answer with clear understanding of complexity.",
                "Add concrete edge cases and mention sorted input assumptions."
        );

        when(aiInterviewService.evaluateResponse(request.question(), request.userResponse())).thenReturn(expected);

        ResponseEntity<EvaluationResult> response = controller.evaluate(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expected, response.getBody());
    }
}


