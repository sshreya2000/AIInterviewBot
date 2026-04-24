package com.company.aiinterview.ai.service;

import com.company.aiinterview.ai.dto.request.EvaluateAnswerRequestDto;
import com.company.aiinterview.ai.dto.request.GenerateQuestionRequestDto;

public interface PromptBuilderService {
    String buildResumeExtractionPrompt(String resumeText);
    String buildJdExtractionPrompt(String jdText);
    String buildQuestionPrompt(GenerateQuestionRequestDto request);
    String buildEvaluationPrompt(EvaluateAnswerRequestDto request);
    String buildFinalSummaryPrompt(String roleApplied, Integer yearsOfExperience, String transcript, String categoryScores);
    String buildResumeAuditPrompt(String resumeText, String parsedJson);
}
