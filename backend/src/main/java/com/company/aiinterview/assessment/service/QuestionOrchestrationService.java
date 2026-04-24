package com.company.aiinterview.assessment.service;

import com.company.aiinterview.assessment.dto.response.NextQuestionResponseDto;

public interface QuestionOrchestrationService {
    NextQuestionResponseDto nextQuestion(Long sessionId);
}
