package com.company.aiinterview.ai.service.impl;

import com.company.aiinterview.ai.client.GeminiClient;
import com.company.aiinterview.ai.dto.request.GeminiGenerateRequestDto;
import com.company.aiinterview.ai.service.PromptBuilderService;
import com.company.aiinterview.ai.service.SkillExtractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillExtractionServiceImpl implements SkillExtractionService {

    private final GeminiClient geminiClient;
    private final PromptBuilderService promptBuilderService;

    @Override
    public String extractFromResume(String resumeText) {
        String prompt = promptBuilderService.buildResumeExtractionPrompt(resumeText);
        return geminiClient.generateContent(
                GeminiGenerateRequestDto.builder().prompt(prompt).temperature(0.2).maxOutputTokens(512).build()
        ).getRawResponse();
    }

    @Override
    public String extractFromJd(String jdText) {
        String prompt = promptBuilderService.buildJdExtractionPrompt(jdText);
        return geminiClient.generateContent(
                GeminiGenerateRequestDto.builder().prompt(prompt).temperature(0.2).maxOutputTokens(512).build()
        ).getRawResponse();
    }
}
