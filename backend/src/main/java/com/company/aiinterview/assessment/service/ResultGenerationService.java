package com.company.aiinterview.assessment.service;

import com.company.aiinterview.assessment.dto.response.InterviewResultResponseDto;

public interface ResultGenerationService {
    InterviewResultResponseDto generate(Long sessionId);
    InterviewResultResponseDto getResult(Long sessionId);
}
