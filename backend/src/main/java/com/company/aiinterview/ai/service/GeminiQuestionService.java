package com.company.aiinterview.ai.service;

import com.company.aiinterview.ai.dto.request.GenerateQuestionRequestDto;
import com.company.aiinterview.ai.dto.response.GenerateQuestionResponseDto;

public interface GeminiQuestionService {
    GenerateQuestionResponseDto generateQuestion(GenerateQuestionRequestDto request);
}
