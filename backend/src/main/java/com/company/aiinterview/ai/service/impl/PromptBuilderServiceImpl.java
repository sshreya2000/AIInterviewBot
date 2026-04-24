package com.company.aiinterview.ai.service.impl;

import com.company.aiinterview.ai.dto.request.EvaluateAnswerRequestDto;
import com.company.aiinterview.ai.dto.request.GenerateQuestionRequestDto;
import com.company.aiinterview.ai.service.PromptBuilderService;
import com.company.aiinterview.ai.service.PromptTemplateSamples;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptBuilderServiceImpl implements PromptBuilderService {

    @Override
    public String buildResumeExtractionPrompt(String resumeText) {
        return PromptTemplateSamples.RESUME_EXTRACTION_TEMPLATE
                .replace("{{resumeText}}", resumeText);
    }

    @Override
    public String buildJdExtractionPrompt(String jdText) {
        return PromptTemplateSamples.JD_EXTRACTION_TEMPLATE
                .replace("{{jobDescriptionText}}", jdText);
    }

    @Override
    public String buildQuestionPrompt(GenerateQuestionRequestDto r) {
        return PromptTemplateSamples.QUESTION_GENERATION_TEMPLATE
                .replace("{{yearsOfExperience}}", String.valueOf(r.getYearsOfExperience()))
                .replace("{{resumeSkills}}", joinList(r.getResumeSkills()))
                .replace("{{inferredSkills}}", joinList(r.getInferredSkills()))
                .replace("{{jdSkills}}", joinList(r.getJdSkills()))
                .replace("{{category}}", r.getCategory())
                .replace("{{difficulty}}", r.getDifficulty())
                .replace("{{previousQuestions}}", joinList(r.getPreviousQuestions()))
                .replace("{{answerHistorySummary}}", r.getAnswerHistorySummary() != null ? r.getAnswerHistorySummary() : "None");
    }

    @Override
    public String buildEvaluationPrompt(EvaluateAnswerRequestDto r) {
        return PromptTemplateSamples.EVALUATION_TEMPLATE
                .replace("{{questionText}}", r.getQuestion())
                .replace("{{category}}", r.getCategory())
                .replace("{{difficulty}}", r.getDifficulty())
                .replace("{{yearsOfExperience}}", String.valueOf(r.getYearsOfExperience()))
                .replace("{{answerText}}", r.getAnswer());
    }

    @Override
    public String buildFinalSummaryPrompt(String roleApplied, Integer yearsOfExperience, String transcript, String categoryScores) {
        return PromptTemplateSamples.FINAL_SUMMARY_TEMPLATE
                .replace("{{roleApplied}}", roleApplied)
                .replace("{{yearsOfExperience}}", String.valueOf(yearsOfExperience))
                .replace("{{interviewTranscript}}", transcript)
                .replace("{{categoryScores}}", categoryScores);
    }

    @Override
    public String buildResumeAuditPrompt(String resumeText, String parsedJson) {
        return PromptTemplateSamples.RESUME_AUDIT_TEMPLATE
                .replace("{{resume_text}}", resumeText)
                .replace("{{json_output}}", parsedJson);
    }

    private String joinList(List<String> list) {
        return list == null || list.isEmpty() ? "None" : String.join(", ", list);
    }
}
