package com.company.aiinterview.ai.service.impl;

import com.company.aiinterview.ai.client.GeminiClient;
import com.company.aiinterview.ai.dto.request.GeminiGenerateRequestDto;
import com.company.aiinterview.ai.dto.request.ResumeAuditRequestDto;
import com.company.aiinterview.ai.dto.response.ResumeAuditResponseDto;
import com.company.aiinterview.ai.service.PromptBuilderService;
import com.company.aiinterview.ai.service.ResumeAuditService;
import com.company.aiinterview.exception.ExternalServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeAuditServiceImpl implements ResumeAuditService {

    private final GeminiClient geminiClient;
    private final PromptBuilderService promptBuilderService;
    private final ObjectMapper objectMapper;

    @Override
    public ResumeAuditResponseDto audit(ResumeAuditRequestDto request) {
        String prompt = promptBuilderService.buildResumeAuditPrompt(
                request.getResumeText(), request.getParsedJson());

        String raw = geminiClient.generateContent(
                GeminiGenerateRequestDto.builder()
                        .prompt(prompt)
                        .temperature(0.2)
                        .maxOutputTokens(1024)
                        .build()
        ).getRawResponse();

        try {
            int start = raw.indexOf('{');
            int end = raw.lastIndexOf('}');
            if (start == -1 || end == -1) throw new ExternalServiceException("No JSON in audit response");
            JsonNode node = objectMapper.readTree(raw.substring(start, end + 1));

            return ResumeAuditResponseDto.builder()
                    .score(node.path("score").asInt(0))
                    .isProductionReady(node.path("is_production_ready").asBoolean(false))
                    .issues(parseArray(node.path("issues")))
                    .missingFeatures(parseArray(node.path("missing_features")))
                    .improvements(parseArray(node.path("improvements")))
                    .build();
        } catch (Exception e) {
            throw new ExternalServiceException("Resume audit failed: " + e.getMessage());
        }
    }

    private List<String> parseArray(JsonNode node) {
        List<String> result = new ArrayList<>();
        if (node != null && node.isArray()) {
            node.forEach(n -> {
                String val = n.asText("").trim();
                if (!val.isEmpty()) result.add(val);
            });
        }
        return result;
    }
}
