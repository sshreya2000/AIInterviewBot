package com.company.aiinterview.assessment.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InterviewResultResponseDto { Long sessionId; Double communication; Double techstack; Double coding; Double problemSolving; Double confidence; Double resumeRelevance; Double overallScore; String recommendation; java.util.List<String> strengths; java.util.List<String> weaknesses; String summary; java.util.List<CategoryScoreResponseDto> categoryScores; }
