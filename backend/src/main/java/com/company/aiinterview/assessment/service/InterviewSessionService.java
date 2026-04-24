package com.company.aiinterview.assessment.service;

import com.company.aiinterview.assessment.dto.request.StartInterviewRequestDto;
import com.company.aiinterview.assessment.dto.request.SubmitTextAnswerRequestDto;
import com.company.aiinterview.assessment.dto.response.StartInterviewResponseDto;
import com.company.aiinterview.assessment.dto.response.SubmitAnswerResponseDto;

public interface InterviewSessionService {
    StartInterviewResponseDto startSession(StartInterviewRequestDto request);
    SubmitAnswerResponseDto submitTextAnswer(Long sessionId, SubmitTextAnswerRequestDto request);
    void completeSession(Long sessionId);
}
