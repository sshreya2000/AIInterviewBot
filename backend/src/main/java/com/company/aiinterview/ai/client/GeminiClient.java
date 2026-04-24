package com.company.aiinterview.ai.client;

import com.company.aiinterview.ai.dto.request.GeminiGenerateRequestDto;
import com.company.aiinterview.ai.dto.response.GeminiGenerateResponseDto;
import com.company.aiinterview.ai.service.GeminiRateLimiter;
import com.company.aiinterview.config.GeminiConfig;
import com.company.aiinterview.exception.ExternalServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final RestClient geminiRestClient;
    private final GeminiConfig geminiConfig;
    private final ObjectMapper objectMapper;
    private final GeminiRateLimiter rateLimiter;

    @Value("${gemini.max-retries:3}")
    private int maxRetries;

    @Value("${gemini.initial-backoff-ms:1000}")
    private long initialBackoffMs;

    private static final double BACKOFF_MULTIPLIER = 2.0;

    public GeminiGenerateResponseDto generateContent(GeminiGenerateRequestDto request) {
        try {
            return rateLimiter.executeWithRateLimit(() -> generateContentWithRetry(request, 0));
        } catch (Exception e) {
            log.error("Failed to generate content after rate limiting", e);
            throw new ExternalServiceException("Failed to generate content: " + e.getMessage());
        }
    }

    private GeminiGenerateResponseDto generateContentWithRetry(GeminiGenerateRequestDto request, int attempt) {
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", request.getPrompt())))),
                "generationConfig", Map.of(
                        "temperature", request.getTemperature() != null ? request.getTemperature() : 0.7,
                        "maxOutputTokens", request.getMaxOutputTokens() != null ? request.getMaxOutputTokens() : 1024
                )
        );

        String url = "/v1beta/models/" + geminiConfig.getModel() + ":generateContent?key=" + geminiConfig.getApiKey();

        try {
            log.debug("Making Gemini API request (attempt {}/{})", attempt + 1, maxRetries + 1);
            
            String response = geminiRestClient.post()
                    .uri(url)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            if (response == null) {
                throw new ExternalServiceException("Empty response from Gemini API");
            }

            JsonNode root = objectMapper.readTree(response);
            
            // Check for API errors in response
            if (root.has("error")) {
                String errorMessage = root.path("error").path("message").asText("Unknown error");
                log.error("Gemini API error: {}", errorMessage);
                throw new ExternalServiceException("Gemini API error: " + errorMessage);
            }

            JsonNode candidates = root.path("candidates");
            if (candidates.isEmpty()) {
                throw new ExternalServiceException("No candidates in Gemini response");
            }

            String text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            if (text.isEmpty()) {
                throw new ExternalServiceException("Empty text in Gemini response");
            }

            // Strip markdown code fences Gemini sometimes wraps around JSON
            text = text.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```\\s*", "").trim();

            log.debug("Successfully generated content from Gemini API");
            return GeminiGenerateResponseDto.builder()
                    .rawResponse(text)
                    .model(geminiConfig.getModel())
                    .generatedAt(Instant.now())
                    .build();
                    
        } catch (HttpClientErrorException e) {
            // Handle 429 Too Many Requests specifically
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                log.warn("Rate limited by Gemini API (429). Attempt {}/{}", attempt + 1, maxRetries + 1);
                
                if (attempt < maxRetries) {
                    long backoffMs = (long) (initialBackoffMs * Math.pow(BACKOFF_MULTIPLIER, attempt));
                    log.warn("Retrying after {}ms backoff", backoffMs);
                    
                    try {
                        Thread.sleep(backoffMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry sleep interrupted");
                        throw new ExternalServiceException("Rate limit retry interrupted");
                    }
                    
                    // Recursive retry
                    return generateContentWithRetry(request, attempt + 1);
                } else {
                    log.error("Max retries ({}) exceeded for rate limit", maxRetries);
                    throw new ExternalServiceException(
                        "Rate limited after " + maxRetries + " retries. Please try again later."
                    );
                }
            }
            
            // Handle other HTTP errors
            log.error("Gemini API HTTP error {}: {}", e.getStatusCode(), e.getMessage());
            throw new ExternalServiceException("Gemini API call failed: " + e.getMessage());
            
        } catch (Exception e) {
            log.error("Gemini API call failed", e);
            throw new ExternalServiceException("Gemini API call failed: " + e.getMessage());
        }
    }
}
