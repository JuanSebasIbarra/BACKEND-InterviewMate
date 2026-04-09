package com.interviewmate.InterviewMate.service;

import com.interviewmate.InterviewMate.dto.StartStudyRequest;
import com.interviewmate.InterviewMate.dto.StudySessionResponse;
import com.interviewmate.InterviewMate.dto.StudySessionSummaryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface StudyService {
    StudySessionResponse start(StartStudyRequest request);
    StudySessionResponse getById(UUID studySessionId);
    List<StudySessionSummaryResponse> getByAuthenticatedUser();
    String transcribeAudio(MultipartFile audioFile);
}



