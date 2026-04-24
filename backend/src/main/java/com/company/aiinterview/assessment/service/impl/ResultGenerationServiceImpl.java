package com.company.aiinterview.assessment.service.impl;

import com.company.aiinterview.ai.service.PromptBuilderService;
import com.company.aiinterview.ai.client.GeminiClient;
import com.company.aiinterview.ai.dto.request.GeminiGenerateRequestDto;
import com.company.aiinterview.assessment.dto.response.CategoryScoreResponseDto;
import com.company.aiinterview.assessment.dto.response.InterviewResultResponseDto;
import com.company.aiinterview.assessment.entity.CategoryScoreEntity;
import com.company.aiinterview.assessment.entity.InterviewAnswerEntity;
import com.company.aiinterview.assessment.entity.InterviewResultEntity;
import com.company.aiinterview.assessment.entity.InterviewSessionEntity;
import com.company.aiinterview.assessment.repository.CategoryScoreRepository;
import com.company.aiinterview.assessment.repository.InterviewAnswerRepository;
import com.company.aiinterview.assessment.repository.InterviewQuestionRepository;
import com.company.aiinterview.assessment.repository.InterviewResultRepository;
import com.company.aiinterview.assessment.repository.InterviewSessionRepository;
import com.company.aiinterview.assessment.service.ResultGenerationService;
import com.company.aiinterview.exception.ExternalServiceException;
import com.company.aiinterview.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultGenerationServiceImpl implements ResultGenerationService {

    private static final Map<String, Double> WEIGHTS = Map.of(
            "TECHNICAL", 0.30,
            "PROBLEM_SOLVING", 0.25,
            "CODING", 0.20,
            "RESUME_RELEVANCE", 0.15,
            "COMMUNICATION", 0.10
    );

    private final InterviewSessionRepository sessionRepository;
    private final InterviewAnswerRepository answerRepository;
    private final InterviewQuestionRepository questionRepository;
    private final InterviewResultRepository resultRepository;
    private final CategoryScoreRepository categoryScoreRepository;
    private final GeminiClient geminiClient;
    private final PromptBuilderService promptBuilderService;
    private final ObjectMapper objectMapper;

    @Override
    public InterviewResultResponseDto generate(Long sessionId) {
        InterviewSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));

        List<InterviewAnswerEntity> answers = answerRepository.findBySessionId(sessionId);

        // Group scores by category
        Map<String, List<Double>> scoresByCategory = answers.stream()
                .collect(Collectors.groupingBy(
                        a -> categoryForQuestion(a.getQuestionId()),
                        Collectors.mapping(a -> a.getScore() != null ? a.getScore() : 0.0, Collectors.toList())
                ));

        // Compute weighted overall score
        double overallScore = WEIGHTS.entrySet().stream()
                .mapToDouble(e -> {
                    List<Double> scores = scoresByCategory.getOrDefault(e.getKey(), List.of(5.0));
                    double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(5.0);
                    return avg * e.getValue();
                }).sum();

        // Build transcript for Gemini
        String transcript = answers.stream()
                .map(a -> "Q: " + getQuestionText(a.getQuestionId()) + "\nA: " + a.getAnswerText() + "\nScore: " + a.getScore())
                .collect(Collectors.joining("\n\n"));

        String catScoresSummary = scoresByCategory.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0))
                .collect(Collectors.joining(", "));

        String prompt = promptBuilderService.buildFinalSummaryPrompt(
                "Software Engineer", 0, transcript, catScoresSummary);

        String raw = geminiClient.generateContent(
                GeminiGenerateRequestDto.builder().prompt(prompt).temperature(0.3).maxOutputTokens(1024).build()
        ).getRawResponse();

        String recommendation = resolveRecommendation(overallScore);
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        String summary = "";

        try {
            String json = extractJson(raw);
            JsonNode node = objectMapper.readTree(json);
            node.path("strengths").forEach(n -> strengths.add(n.asText()));
            node.path("weaknesses").forEach(n -> weaknesses.add(n.asText()));
            summary = node.path("summary").asText();
        } catch (Exception ignored) {}

        InterviewResultEntity result = resultRepository.save(InterviewResultEntity.builder()
                .sessionId(sessionId)
                .overallScore(overallScore)
                .recommendation(recommendation)
                .strengths(String.join(",", strengths))
                .weaknesses(String.join(",", weaknesses))
                .summary(summary)
                .build());

        List<CategoryScoreResponseDto> catScoreDtos = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : scoresByCategory.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            categoryScoreRepository.save(CategoryScoreEntity.builder()
                    .resultId(result.getId())
                    .category(entry.getKey())
                    .score(avg)
                    .remarks(avg >= 7.0 ? "Good" : avg >= 5.0 ? "Average" : "Needs Improvement")
                    .build());
            catScoreDtos.add(CategoryScoreResponseDto.builder()
                    .category(entry.getKey()).score(avg)
                    .remarks(avg >= 7.0 ? "Good" : avg >= 5.0 ? "Average" : "Needs Improvement")
                    .build());
        }

        return buildResponseDto(result, catScoreDtos);
    }

    @Override
    public InterviewResultResponseDto getResult(Long sessionId) {
        InterviewResultEntity result = resultRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found for session: " + sessionId));
        List<CategoryScoreResponseDto> catScores = categoryScoreRepository.findByResultId(result.getId()).stream()
                .map(c -> CategoryScoreResponseDto.builder()
                        .category(c.getCategory()).score(c.getScore()).remarks(c.getRemarks()).build())
                .collect(Collectors.toList());
        return buildResponseDto(result, catScores);
    }

    private InterviewResultResponseDto buildResponseDto(InterviewResultEntity result, List<CategoryScoreResponseDto> catScores) {
        return InterviewResultResponseDto.builder()
                .sessionId(result.getSessionId())
                .overallScore(result.getOverallScore())
                .recommendation(result.getRecommendation())
                .strengths(List.of(result.getStrengths() != null ? result.getStrengths().split(",") : new String[0]))
                .weaknesses(List.of(result.getWeaknesses() != null ? result.getWeaknesses().split(",") : new String[0]))
                .summary(result.getSummary())
                .categoryScores(catScores)
                .build();
    }

    private String resolveRecommendation(double score) {
        if (score >= 7.5) return "HIRE";
        if (score >= 5.0) return "HOLD";
        return "REJECT";
    }

    private String categoryForQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .map(q -> q.getCategory() != null ? q.getCategory() : "TECHNICAL")
                .orElse("TECHNICAL");
    }

    private String getQuestionText(Long questionId) {
        return questionRepository.findById(questionId)
                .map(q -> q.getQuestionText() != null ? q.getQuestionText() : "")
                .orElse("");
    }

    private String extractJson(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start == -1 || end == -1) throw new ExternalServiceException("No JSON in summary response");
        return raw.substring(start, end + 1);
    }
}
