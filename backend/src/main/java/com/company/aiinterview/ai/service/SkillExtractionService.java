package com.company.aiinterview.ai.service;

import com.company.aiinterview.ai.dto.request.EvaluateAnswerRequestDto;
import com.company.aiinterview.ai.dto.request.ExtractSkillsRequestDto;
import com.company.aiinterview.ai.dto.request.GenerateQuestionRequestDto;

public interface SkillExtractionService {
    String extractFromResume(String resumeText);
    String extractFromJd(String jdText);
}
