package com.company.aiinterview.ai.client;

import com.company.aiinterview.ai.dto.request.GeminiGenerateRequestDto;
import com.company.aiinterview.ai.dto.response.GeminiGenerateResponseDto;
import org.springframework.stereotype.Component;

@Component
public class GeminiClient {
    public GeminiGenerateResponseDto generateContent(GeminiGenerateRequestDto request) {
        // TODO: Call Gemini REST API using RestTemplate/WebClient.
        return new GeminiGenerateResponseDto();
    }
}
