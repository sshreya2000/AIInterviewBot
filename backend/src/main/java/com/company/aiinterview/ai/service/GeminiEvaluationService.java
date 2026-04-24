package com.company.aiinterview.ai.service;

import com.company.aiinterview.ai.dto.request.EvaluateAnswerRequestDto;
import com.company.aiinterview.ai.dto.response.EvaluateAnswerResponseDto;

public interface GeminiEvaluationService {
    EvaluateAnswerResponseDto evaluate(EvaluateAnswerRequestDto request);
}
