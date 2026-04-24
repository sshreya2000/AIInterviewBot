package com.company.aiinterview.ai.service.impl;

import com.company.aiinterview.ai.client.GeminiClient;
import com.company.aiinterview.ai.dto.request.EvaluateAnswerRequestDto;
import com.company.aiinterview.ai.dto.request.GeminiGenerateRequestDto;
import com.company.aiinterview.ai.dto.response.EvaluateAnswerResponseDto;
import com.company.aiinterview.ai.service.GeminiEvaluationService;
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
public class GeminiEvaluationServiceImpl implements GeminiEvaluationService {

    private final GeminiClient geminiClient;
    private final PromptBuilderService promptBuilderService;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "gemini_evaluations", 
               key = "#request.answer + '_' + #request.question + '_' + #request.difficulty")
    public EvaluateAnswerResponseDto evaluate(EvaluateAnswerRequestDto request) {
        String prompt = promptBuilderService.buildEvaluationPrompt(request);
        String raw = geminiClient.generateContent(
                GeminiGenerateRequestDto.builder().prompt(prompt).temperature(0.3).maxOutputTokens(512).build()
        ).getRawResponse();
        try {
            String json = extractJson(raw);
            JsonNode node = objectMapper.readTree(json);
            List<String> strengths = new ArrayList<>();
            List<String> improvements = new ArrayList<>();
            node.path("strengths").forEach(n -> strengths.add(n.asText()));
            node.path("improvements").forEach(n -> improvements.add(n.asText()));
            return EvaluateAnswerResponseDto.builder()
                    .score(node.path("score").asDouble())
                    .feedback(node.path("feedback").asText())
                    .strengths(strengths)
                    .improvements(improvements)
                    .build();
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to parse evaluation response: " + e.getMessage());
        }
    }

    private String extractJson(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start == -1 || end == -1) throw new ExternalServiceException("No JSON found in Gemini response");
        return raw.substring(start, end + 1);
    }
}
