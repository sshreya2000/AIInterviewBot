package com.company.aiinterview.ai.service.impl;

import com.company.aiinterview.ai.client.GeminiClient;
import com.company.aiinterview.ai.dto.request.GeminiGenerateRequestDto;
import com.company.aiinterview.ai.dto.request.GenerateQuestionRequestDto;
import com.company.aiinterview.ai.dto.response.GenerateQuestionResponseDto;
import com.company.aiinterview.ai.service.GeminiQuestionService;
import com.company.aiinterview.ai.service.PromptBuilderService;
import com.company.aiinterview.exception.ExternalServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiQuestionServiceImpl implements GeminiQuestionService {

    private final GeminiClient geminiClient;
    private final PromptBuilderService promptBuilderService;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "gemini_questions", 
               key = "#request.jobDescription + '_' + #request.difficulty + '_' + #request.experienceLevel")
    public GenerateQuestionResponseDto generateQuestion(GenerateQuestionRequestDto request) {
        String prompt = promptBuilderService.buildQuestionPrompt(request);
        String raw = geminiClient.generateContent(
                GeminiGenerateRequestDto.builder().prompt(prompt).temperature(0.7).maxOutputTokens(512).build()
        ).getRawResponse();
        try {
            String json = extractJson(raw);
            JsonNode node = objectMapper.readTree(json);
            List<String> focusSkills = new ArrayList<>();
            node.path("focusSkills").forEach(n -> focusSkills.add(n.asText()));
            return GenerateQuestionResponseDto.builder()
                    .questionText(node.path("questionText").asText())
                    .category(node.path("category").asText())
                    .difficulty(node.path("difficulty").asText())
                    .focusSkills(focusSkills)
                    .build();
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to parse question response: " + e.getMessage());
        }
    }

    private String extractJson(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start == -1 || end == -1) throw new ExternalServiceException("No JSON found in Gemini response");
        return raw.substring(start, end + 1);
    }
}
